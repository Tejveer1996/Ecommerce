package dev.Tejveer.EcomProductService.Services.Impl;

import dev.Tejveer.EcomProductService.DTO.Product.ProductAddRequest;
import dev.Tejveer.EcomProductService.DTO.Product.ProductResponse;
import dev.Tejveer.EcomProductService.DTO.Product.ProductUpdateRequest;
import dev.Tejveer.EcomProductService.Entity.Category;
import dev.Tejveer.EcomProductService.Entity.Product;
import dev.Tejveer.EcomProductService.Entity.ProductFilter;
import dev.Tejveer.EcomProductService.Entity.ProductStatus;
import dev.Tejveer.EcomProductService.Exception.ResourceNotFoundException;
import dev.Tejveer.EcomProductService.Repository.CategoryRepository;
import dev.Tejveer.EcomProductService.Repository.ProductRepository;
import dev.Tejveer.EcomProductService.Services.ProductServices;
import dev.Tejveer.EcomProductService.Utils.ProductSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductServices {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ModelMapper modelMapper;

    @Override
    public ProductResponse addProduct(ProductAddRequest productAddRequest) throws ResourceNotFoundException {
        Category category = categoryRepository.findById(productAddRequest.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException("Invalid category id")
        );
        Product product = modelMapper.map(productAddRequest, Product.class);
        product.setCategory(category);
        product.setStatus(ProductStatus.DRAFT);
        Product savedProduct = productRepository.save(product);
        ProductResponse productResponse = modelMapper.map(savedProduct, ProductResponse.class);
        productResponse.setCategoryName(category.getName());
        return productResponse;
    }

    @Override
    public ProductResponse updateProduct(ProductUpdateRequest updateRequest, UUID sellerId) throws ResourceNotFoundException {
        Product product = productRepository.findById(updateRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getSellerId().equals(sellerId)) {
            throw new IllegalAccessError("Given seller is not allowed to delete this product");
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

    @Override
    public boolean deleteProduct(UUID productId, UUID sellerId) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (product.getSellerId().equals(sellerId)) {
            throw new IllegalAccessError("Given seller is not allowed to delete this product");
        }
        productRepository.delete(product);
        return true;
    }

    @Override
    public ProductResponse getProductById(UUID productId) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        response.setCategoryName(product.getCategory().getName());
        return response;
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(
                product -> modelMapper.map(product, ProductResponse.class)
        );
    }

    @Override
    public Page<ProductResponse> getFilterProducts(Pageable pageable, ProductFilter filter) {
        Specification<Product> spec = ProductSpecification.withFilter(filter);
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(product -> modelMapper.map(product, ProductResponse.class));
    }

    @Override
    public Page<ProductResponse> keywordSearch(Pageable pageable, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            Page<Product> productPage = productRepository.findAll(pageable);
            return productPage.map(product -> modelMapper.map(product, ProductResponse.class));
        }
        Page<Product> productPage = productRepository.searchByKeyword(keyword, pageable);
        return productPage.map(product -> modelMapper.map(product, ProductResponse.class));
    }
}
