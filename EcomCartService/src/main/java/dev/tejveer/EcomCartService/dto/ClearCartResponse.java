package dev.tejveer.EcomCartService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClearCartResponse {
    UUID cartId;
    boolean cleared;
    String message;
}
