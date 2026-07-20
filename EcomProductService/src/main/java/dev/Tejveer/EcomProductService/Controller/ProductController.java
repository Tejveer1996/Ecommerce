package dev.Tejveer.EcomProductService.Controller;

import dev.Tejveer.EcomProductService.DTO.Product.ProductAddRequest;
import dev.Tejveer.EcomProductService.DTO.Product.ProductResponse;
import dev.Tejveer.EcomProductService.DTO.Product.ProductUpdateRequest;
import dev.Tejveer.EcomProductService.Entity.ProductFilter;
import dev.Tejveer.EcomProductService.Services.ProductServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/apis/products")
public class ProductController {

    private static final String ERROR_MESSAGE = "Something went wrong";

    private final ProductServices productServices;

    public ProductController(ProductServices productServices) {
        this.productServices = productServices;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductAddRequest request) {
        try {
            UUID sellerId = getCurrentSellerId();
            request.setSellerId(sellerId);
            ProductResponse response = productServices.addProduct(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while adding new product, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody ProductUpdateRequest request) {
        try {
            UUID sellerId = getCurrentSellerId();
            ProductResponse response = productServices.updateProduct(request, sellerId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while updating product, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID productId) {
        try {
            UUID sellerId = getCurrentSellerId();
            productServices.deleteProduct(productId, sellerId);
            return ResponseEntity.ok("Successfully deleted product, productId :" + productId);
        } catch (Exception e) {
            log.error("Error occurred while deleting product :{}, error :: {}", productId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID productId) {
        try {
            ProductResponse response = productServices.getProductById(productId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while getting product :{}, error :: {}", productId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        try {
            Page<ProductResponse> response = productServices.getAllProducts(pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while getting all products, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductResponse>> getFilterProducts(Pageable pageable, ProductFilter filter) {
        try {
            Page<ProductResponse> response = productServices.getFilterProducts(pageable, filter);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while filtering products, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> keywordSearch(Pageable pageable, @RequestParam String keyword) {
        try {
            Page<ProductResponse> response = productServices.keywordSearch(pageable, keyword);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while searching products with keyword :{}, error :: {}", keyword, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    private UUID getCurrentSellerId() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UUID.fromString(principal);
    }
}
