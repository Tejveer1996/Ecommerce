package dev.Tejveer.EcomProductService.DTO.Product;

import dev.Tejveer.EcomProductService.Entity.CurrencyType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    @NotBlank(message = "Product id cannot be null for update")
    private UUID productId;

    private String name;

    private String description;

    private UUID categoryId;

    private BigDecimal price;

    private String brand;

    private BigDecimal weight;

    private CurrencyType currencyType;
}
