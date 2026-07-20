package dev.Tejveer.EcomProductService.DTO.Image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageAddRequest {

    private UUID productId;

    private String imageUrl;

    private Integer displayOrder;

    @Builder.Default
    private boolean isPrimary = false;

    @Builder.Default
    private boolean isThumbnail = false;
}
