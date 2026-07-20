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
public class ProductImageResponse {
    private UUID id;
    private UUID productId;
    private String imageUrl;
    private int displayOrder;
    private boolean isPrimary;
    private boolean isThumbnail;
}
