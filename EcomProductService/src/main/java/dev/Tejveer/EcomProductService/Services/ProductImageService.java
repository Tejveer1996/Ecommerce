package dev.Tejveer.EcomProductService.Services;


import dev.Tejveer.EcomProductService.DTO.Image.ProductImageAddRequest;
import dev.Tejveer.EcomProductService.DTO.Image.ProductImageResponse;
import dev.Tejveer.EcomProductService.Entity.Product;
import dev.Tejveer.EcomProductService.Entity.ProductImage;
import dev.Tejveer.EcomProductService.Exception.ResourceNotFoundException;
import dev.Tejveer.EcomProductService.Repository.ProductImageRepository;
import dev.Tejveer.EcomProductService.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;


    public ProductImageResponse addImage(ProductImageAddRequest request) throws ResourceNotFoundException {
        Product product = getProductOrThrow(request.getProductId());
        ProductImage saved = productImageRepository.save(buildEntity(product, request));
        return toResponse(saved);
    }


    public List<ProductImageResponse> addImages(List<ProductImageAddRequest> requests) throws ResourceNotFoundException {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        // Cache products so we don't hit the DB once per request when they
        // share the same productId, and fail fast if any product is missing.
        Map<UUID, Product> productCache = new HashMap<>();
        for (ProductImageAddRequest request : requests) {
            UUID productId = request.getProductId();
            if (!productCache.containsKey(productId)) {
                productCache.put(productId, getProductOrThrow(productId));
            }
        }

        List<ProductImage> entities = new ArrayList<>();
        for (ProductImageAddRequest request : requests) {
            Product product = productCache.get(request.getProductId());
            entities.add(buildEntity(product, request));
        }

        List<ProductImage> saved = productImageRepository.saveAll(entities);
        return saved.stream().map(this::toResponse).collect(Collectors.toList());
    }


    public boolean deleteImage(UUID imageId) throws ResourceNotFoundException {
        ProductImage image = getImageOrThrow(imageId);
        productImageRepository.delete(image);
        return true;
    }


    @Transactional(readOnly = true)
    public List<ProductImageResponse> getImagesByProductId(UUID productId) throws ResourceNotFoundException {
        getProductOrThrow(productId);
        return productImageRepository.findByProduct_IdOrderByDisplayOrderAsc(productId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    public ProductImageResponse setPrimaryImage(UUID productId, UUID imageId) throws ResourceNotFoundException {
        getProductOrThrow(productId);
        ProductImage target = getImageForProductOrThrow(productId, imageId);

        // Clear any existing primary flag(s) for this product.
        List<ProductImage> currentPrimaries = productImageRepository.findByProduct_IdAndIsPrimaryTrue(productId);
        for (ProductImage image : currentPrimaries) {
            if (!image.getId().equals(imageId)) {
                image.setPrimary(false);
            }
        }
        productImageRepository.saveAll(currentPrimaries);

        target.setPrimary(true);
        ProductImage saved = productImageRepository.save(target);
        return toResponse(saved);
    }


    public ProductImageResponse setThumbnailImage(UUID productId, UUID imageId) throws ResourceNotFoundException {
        getProductOrThrow(productId);
        ProductImage target = getImageForProductOrThrow(productId, imageId);

        List<ProductImage> currentThumbnails = productImageRepository.findByProduct_IdAndIsThumbnailTrue(productId);
        for (ProductImage image : currentThumbnails) {
            if (!image.getId().equals(imageId)) {
                image.setThumbnail(false);
            }
        }
        productImageRepository.saveAll(currentThumbnails);

        target.setThumbnail(true);
        ProductImage saved = productImageRepository.save(target);
        return toResponse(saved);
    }


    public List<ProductImageResponse> reorderImages(UUID productId, List<UUID> orderedImageIds) throws ResourceNotFoundException {
        getProductOrThrow(productId);

        List<ProductImage> existingImages = productImageRepository.findByProduct_IdOrderByDisplayOrderAsc(productId);
        Map<UUID, ProductImage> imagesById = existingImages.stream()
                .collect(Collectors.toMap(ProductImage::getId, image -> image));

        if (orderedImageIds == null || orderedImageIds.size() != existingImages.size()
                || !imagesById.keySet().containsAll(orderedImageIds)) {
            throw new IllegalArgumentException(
                    "orderedImageIds must contain exactly the full set of image IDs currently belonging to product " + productId);
        }

        List<ProductImage> reordered = new ArrayList<>();
        int order = 0;
        for (UUID imageId : orderedImageIds) {
            ProductImage image = imagesById.get(imageId);
            image.setDisplayOrder(order++);
            reordered.add(image);
        }

        List<ProductImage> saved = productImageRepository.saveAll(reordered);
        return saved.stream()
                .sorted((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Product getProductOrThrow(UUID productId) throws ResourceNotFoundException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    private ProductImage getImageOrThrow(UUID imageId) throws ResourceNotFoundException {
        return productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found with id: " + imageId));
    }

    private ProductImage getImageForProductOrThrow(UUID productId, UUID imageId) throws ResourceNotFoundException {
        ProductImage image = getImageOrThrow(imageId);
        if (!image.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException(
                    "Product image " + imageId + " does not belong to product " + productId);
        }
        return image;
    }

    private ProductImage buildEntity(Product product, ProductImageAddRequest request) {
        int displayOrder = request.getDisplayOrder() != null ? request.getDisplayOrder()
                : (int) productImageRepository.countByProduct_Id(product.getId());

        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setImageUrl(request.getImageUrl());
        image.setDisplayOrder(displayOrder);
        image.setPrimary(request.isPrimary());
        image.setThumbnail(request.isThumbnail());
        return image;
    }

    private ProductImageResponse toResponse(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .productId(image.getProduct().getId())
                .imageUrl(image.getImageUrl())
                .displayOrder(image.getDisplayOrder())
                .isPrimary(image.isPrimary())
                .isThumbnail(image.isThumbnail())
                .build();
    }
}
