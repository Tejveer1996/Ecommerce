package dev.Tejveer.EcomProductService.DTO.Product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponseDto {
    List<ProductResponse> productList;
    Integer currentPageElements;
    Long totalElements;
    Long totalPages;
    boolean hasNext;
}
