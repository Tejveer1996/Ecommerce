package dev.Tejveer.EcomProductService.Entity;

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
public class ProductFilter {
    private UUID categoryId;

    private UUID sellerId;

    private String brand;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private CurrencyType currencyType;

    private ProductStatus status;
}
