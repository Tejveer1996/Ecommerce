package dev.tejveer.EcomCartService.service;

import dev.tejveer.EcomCartService.dto.AddItemToCartRequest;
import dev.tejveer.EcomCartService.dto.CartAddItemResponse;
import dev.tejveer.EcomCartService.dto.CartResponse;
import dev.tejveer.EcomCartService.dto.ClearCartResponse;
import dev.tejveer.EcomCartService.dto.RemoveItemFromCartRequest;
import dev.tejveer.EcomCartService.dto.UpdateCartItemQuantityRequest;
import dev.tejveer.EcomCartService.exception.CartCreationException;
import dev.tejveer.EcomCartService.exception.CartItemOperationException;

import java.util.UUID;

public interface CartService {
    CartResponse createCart(UUID userId) throws CartCreationException;

    CartResponse getCartByUserId(UUID userId);

    CartAddItemResponse addItemToCart(UUID userId, AddItemToCartRequest request) throws CartItemOperationException;

    CartResponse updateCartItemQuantity(UUID userId, UpdateCartItemQuantityRequest request);

    CartResponse removeItemFromCart(UUID userId, RemoveItemFromCartRequest request);

    ClearCartResponse clearCart(UUID userId);
}
