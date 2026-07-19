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
@Entity(name = "product_attribute")
@AllArgsConstructor
@NoArgsConstructor
public class ProductAttribute extends BaseModel {
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private String name;
    private String value;
}
