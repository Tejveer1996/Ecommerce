package dev.Tejveer.EcomProductService.Services;

import dev.Tejveer.EcomProductService.DTO.Product.ProductAddRequest;
import dev.Tejveer.EcomProductService.DTO.Product.ProductAttributeRequest;
import dev.Tejveer.EcomProductService.DTO.Product.ProductResponse;
import dev.Tejveer.EcomProductService.DTO.Product.ProductResponseDto;
import dev.Tejveer.EcomProductService.DTO.Product.ProductUpdateRequest;
import dev.Tejveer.EcomProductService.Entity.Category;
import dev.Tejveer.EcomProductService.Entity.Product;
import dev.Tejveer.EcomProductService.Entity.ProductAttribute;
import dev.Tejveer.EcomProductService.Entity.ProductFilter;
import dev.Tejveer.EcomProductService.Entity.ProductStatus;
import dev.Tejveer.EcomProductService.Exception.ResourceNotFoundException;
import dev.Tejveer.EcomProductService.Repository.CategoryRepository;
import dev.Tejveer.EcomProductService.Repository.ProductRepository;
import dev.Tejveer.EcomProductService.Utils.ProductSpecification;
import dev.Tejveer.EcomProductService.client.InventoryFeignClient;
import dev.Tejveer.EcomProductService.client.dto.InventoryCreateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final InventoryFeignClient inventoryFeignClient;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;


    @Transactional
    public ProductResponse addProduct(UUID selleId, ProductAddRequest productAddRequest) throws ResourceNotFoundException {
        Category category = categoryRepository.findById(productAddRequest.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException("Invalid category id")
        );
        Product newProduct = mapFromAddProductRequest(selleId, category, productAddRequest);
        Product savedProduct = productRepository.save(newProduct);

        // initialize inventory
        inventoryFeignClient.initializeInventory(InventoryCreateRequest.builder()
                .productId(savedProduct.getId())
                .availableQuantity(0L)
                .minimumStock(0L)
                .reservedQuantity(0L)
                .build());

        ProductResponse productResponse = modelMapper.map(savedProduct, ProductResponse.class);
        productResponse.setCategoryName(category.getName());
        return productResponse;
    }


    public ProductResponse updateProduct(ProductUpdateRequest updateRequest, UUID sellerId) throws ResourceNotFoundException {
        Product product = productRepository.findById(updateRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new IllegalAccessError("Given seller is not allowed to Update this product");
        }

        if (updateRequest.getName() != null) {
            product.setName(updateRequest.getName());
        }

        if (updateRequest.getDescription() != null) {
            product.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getCategoryId() != null) {

            Category category = categoryRepository.findById(updateRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            product.setCategory(category);
        }

        if (updateRequest.getPrice() != null) {
            product.setPrice(updateRequest.getPrice());
        }

        if (updateRequest.getBrand() != null) {
            product.setBrand(updateRequest.getBrand());
        }

        if (updateRequest.getWeight() != null) {
            product.setWeight(updateRequest.getWeight());
        }

        if (updateRequest.getCurrencyType() != null) {
            product.setCurrencyType(updateRequest.getCurrencyType());
        }

        Product updatedProduct = productRepository.save(product);

        return modelMapper.map(updatedProduct, ProductResponse.class);
    }


    public boolean deleteProduct(UUID productId, UUID sellerId) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.getSellerId().equals(sellerId)) {
            throw new IllegalAccessError("Given seller is not allowed to delete this product");
        }
        productRepository.delete(product);
        return true;
    }


    public ProductResponse getProductById(UUID productId) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        response.setCategoryName(product.getCategory().getName());
        return response;
    }


    public ProductResponseDto getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        Page<ProductResponse> pageData = products.map(
                product -> modelMapper.map(product, ProductResponse.class)
        );

        return ProductResponseDto.builder()
                .productList(pageData.getContent())
                .currentPageElements(pageData.getContent().size())
                .totalElements(pageData.getTotalElements())
                .totalPages((long) pageData.getTotalPages())
                .hasNext(pageData.hasNext())
                .build();
    }


    public ProductResponseDto getFilterProducts(Pageable pageable, ProductFilter filter) {
        Specification<Product> spec = ProductSpecification.withFilter(filter);
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        Page<ProductResponse> pageData = productPage.map(
                product -> modelMapper.map(product, ProductResponse.class)
        );

        return ProductResponseDto.builder()
                .productList(pageData.getContent())
                .currentPageElements(pageData.getContent().size())
                .totalElements(pageData.getTotalElements())
                .totalPages((long) pageData.getTotalPages())
                .hasNext(pageData.hasNext())
                .build();
    }


    public ProductResponseDto keywordSearch(Pageable pageable, String keyword) {
        Page<ProductResponse> pageData;
        if (keyword == null || keyword.isBlank()) {
            Page<Product> productPage = productRepository.findAll(pageable);
            pageData = productPage.map(
                    product -> modelMapper.map(product, ProductResponse.class)
            );
        } else {
            Page<Product> productPage = productRepository.searchByKeyword(keyword, pageable);
            pageData = productPage.map(
                    product -> modelMapper.map(product, ProductResponse.class)
            );
        }
        return ProductResponseDto.builder()
                .productList(pageData.getContent())
                .currentPageElements(pageData.getContent().size())
                .totalElements(pageData.getTotalElements())
                .totalPages((long) pageData.getTotalPages())
                .hasNext(pageData.hasNext())
                .build();
    }

    private Product mapFromAddProductRequest(UUID selleId, Category category, ProductAddRequest request){
       Product product = Product.builder()
                .name(request.getName())
                .brand(request.getBrand())
                .description(request.getDescription())
                .category(category)
                .status(ProductStatus.DRAFT)
                .sellerId(selleId)
                .price(request.getPrice())
                .currencyType(request.getCurrencyType())
                .weight(request.getWeight())
                .build();
        List<ProductAttribute> productAttributeList = new ArrayList<>();
        for (ProductAttributeRequest attributeRequest : request.getProductAttributes()) {
            productAttributeList.add(ProductAttribute.builder()
                    .product(product)
                    .name(attributeRequest.getName())
                    .value(attributeRequest.getValue())
                    .build());
        }
        product.setProductAttributes(productAttributeList);
        return product;
    }
}
