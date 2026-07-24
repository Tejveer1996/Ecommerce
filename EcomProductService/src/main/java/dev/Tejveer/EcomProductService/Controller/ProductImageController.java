package dev.Tejveer.EcomProductService.Controller;

import dev.Tejveer.EcomProductService.DTO.Image.ProductImageAddRequest;
import dev.Tejveer.EcomProductService.DTO.Image.ProductImageResponse;
import dev.Tejveer.EcomProductService.Services.ProductImageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/apis/product-images")
public class ProductImageController {

    private static final String ERROR_MESSAGE = "Something went wrong";

    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductImageResponse> addImage(@Valid @RequestBody ProductImageAddRequest request) {
        try {
            ProductImageResponse response = productImageService.addImage(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error occurred while adding product image, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<ProductImageResponse>> addImages(
            @Valid @RequestBody List<ProductImageAddRequest> requests) {
        try {
            List<ProductImageResponse> response = productImageService.addImages(requests);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error occurred while adding batch product images, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID imageId) {
        try {
            productImageService.deleteImage(imageId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error occurred while deleting product image :{}, error :: {}", imageId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductImageResponse>> getImagesByProductId(@PathVariable UUID productId) {
        try {
            List<ProductImageResponse> response = productImageService.getImagesByProductId(productId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while getting images for product :{}, error :: {}", productId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @PatchMapping("/product/{productId}/primary/{imageId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductImageResponse> setPrimaryImage(
            @PathVariable UUID productId,
            @PathVariable UUID imageId) {
        try {
            ProductImageResponse response = productImageService.setPrimaryImage(productId, imageId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while setting primary image for product :{}, error :: {}", productId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @PatchMapping("/product/{productId}/thumbnail/{imageId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductImageResponse> setThumbnailImage(
            @PathVariable UUID productId,
            @PathVariable UUID imageId) {
        try {
            ProductImageResponse response = productImageService.setThumbnailImage(productId, imageId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while setting thumbnail image for product :{}, error :: {}", productId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @PutMapping("/product/{productId}/reorder")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<ProductImageResponse>> reorderImages(
            @PathVariable UUID productId,
            @RequestBody List<UUID> orderedImageIds) {
        try {
            List<ProductImageResponse> response = productImageService.reorderImages(productId, orderedImageIds);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while reordering images for product :{}, error :: {}", productId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }
}
