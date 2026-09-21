package dev.tejveer.EcomOrderService.Exception;

import dev.tejveer.EcomOrderService.DTO.PriceMismatchDto;

import java.util.List;

public class PriceMismatchException extends RuntimeException {
    private final List<PriceMismatchDto> mismatches;

    public PriceMismatchException(List<PriceMismatchDto> mismatches) {
        super("Price mismatch found for one or more items in the cart.");
        this.mismatches = mismatches;
    }

    public List<PriceMismatchDto> getMismatches() {
        return mismatches;
    }
}
