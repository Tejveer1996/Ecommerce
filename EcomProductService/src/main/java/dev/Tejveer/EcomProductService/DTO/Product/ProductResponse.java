package dev.Tejveer.EcomProductService.DTO.Product;

import dev.Tejveer.EcomProductService.Entity.CurrencyType;
import dev.Tejveer.EcomProductService.Entity.ProductStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID id;

    private String name;

    private String description;

    private UUID sellerId;

    private UUID categoryId;

    private String categoryName;

    private BigDecimal price;

    private String brand;

    private BigDecimal weight;

    private CurrencyType currencyType;

    private ProductStatus status;

    private Instant createdAt;

    private Instant updatedAt;
}
