package dev.Tejveer.EcomProductService.DTO.Product;

import dev.Tejveer.EcomProductService.Entity.CurrencyType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAddRequest {
    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotBlank(message = "Product description cannot be blank")
    private String description;

    @NotBlank(message = "Seller Id is required")
    private UUID sellerId;

    @NotBlank(message = "Category Id is required")
    private UUID categoryId;

    @NotBlank(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Weight is required")
    @DecimalMin(value = "0.01", message = "Weight must be greater than 0")
    private BigDecimal weight;

    @NotBlank(message = "Currency Type is required")
    private CurrencyType currencyType;

    @NotEmpty(message = "Currency Type is required")
    private List<ProductAttributeRequest> productAttributes;
}
