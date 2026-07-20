package dev.Tejveer.EcomProductService.Repository;

import dev.Tejveer.EcomProductService.Entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    List<ProductImage> findByProduct_IdOrderByDisplayOrderAsc(UUID productId);

    long countByProduct_Id(UUID productId);

    List<ProductImage> findByProduct_IdAndIsPrimaryTrue(UUID productId);

    List<ProductImage> findByProduct_IdAndIsThumbnailTrue(UUID productId);
}
