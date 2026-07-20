package dev.Tejveer.EcomProductService.Services;

import dev.Tejveer.EcomProductService.DTO.Product.ProductAddRequest;
import dev.Tejveer.EcomProductService.DTO.Product.ProductResponse;
import dev.Tejveer.EcomProductService.DTO.Product.ProductUpdateRequest;
import dev.Tejveer.EcomProductService.Entity.ProductFilter;
import dev.Tejveer.EcomProductService.Exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductServices {
    // CRUD Operation methods on products
    ProductResponse addProduct(ProductAddRequest productAddRequest) throws ResourceNotFoundException;

    ProductResponse updateProduct(ProductUpdateRequest updateRequest, UUID sellerId) throws ResourceNotFoundException;

    boolean deleteProduct(UUID productId, UUID sellerId) throws ResourceNotFoundException;

    ProductResponse getProductById(UUID productId) throws ResourceNotFoundException;

    /**
     * List of products through pagination, by using pageable concept of JPA
     */
    Page<ProductResponse> getAllProducts(Pageable pageable);

    Page<ProductResponse> getFilterProducts(Pageable pageable, ProductFilter filter);

    Page<ProductResponse> keywordSearch(Pageable pageable, String keyword);
}
