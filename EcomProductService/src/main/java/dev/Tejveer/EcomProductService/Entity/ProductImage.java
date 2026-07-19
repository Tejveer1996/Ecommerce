package dev.Tejveer.EcomProductService.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity(name = "product_images")
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage extends BaseModel{
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String imageUrl;
    private int displayOrder;
    private boolean isPrimary;

    private boolean isThumbnail;
}
