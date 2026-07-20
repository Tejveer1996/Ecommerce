package dev.Tejveer.EcomProductService.Services;

import dev.Tejveer.EcomProductService.DTO.Image.ProductImageAddRequest;
import dev.Tejveer.EcomProductService.DTO.Image.ProductImageResponse;
import dev.Tejveer.EcomProductService.Exception.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

public interface ProductImageService {
    ProductImageResponse addImage(ProductImageAddRequest request) throws ResourceNotFoundException;

    List<ProductImageResponse> addImages(List<ProductImageAddRequest> requests) throws ResourceNotFoundException;

    boolean deleteImage(UUID imageId) throws ResourceNotFoundException;

    List<ProductImageResponse> getImagesByProductId(UUID productId) throws ResourceNotFoundException;

    ProductImageResponse setPrimaryImage(UUID productId, UUID imageId) throws ResourceNotFoundException;

    ProductImageResponse setThumbnailImage(UUID productId, UUID imageId) throws ResourceNotFoundException;

    List<ProductImageResponse> reorderImages(UUID productId, List<UUID> orderedImageIds) throws ResourceNotFoundException;
}
