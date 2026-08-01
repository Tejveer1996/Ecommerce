package dev.tejveer.EcomCartService.service;

import dev.tejveer.EcomCartService.dto.AddItemToCartRequest;
import dev.tejveer.EcomCartService.dto.CartAddItemResponse;
import dev.tejveer.EcomCartService.dto.CartResponse;
import dev.tejveer.EcomCartService.dto.ClearCartResponse;
import dev.tejveer.EcomCartService.dto.RemoveItemFromCartRequest;
import dev.tejveer.EcomCartService.dto.UpdateCartItemQuantityRequest;
import dev.tejveer.EcomCartService.entity.Cart;
import dev.tejveer.EcomCartService.entity.CartItem;
import dev.tejveer.EcomCartService.exception.CartCreationException;
import dev.tejveer.EcomCartService.exception.CartItemOperationException;
import dev.tejveer.EcomCartService.exception.ResourceNotFoundException;
import dev.tejveer.EcomCartService.repository.CartItemRepository;
import dev.tejveer.EcomCartService.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;

    @Override
    public CartResponse createCart(UUID userId) throws CartCreationException {
        try {
            if (cartRepository.existsByUserId(userId)) {
                throw new IllegalArgumentException("Cart already exist for the given user");
            }
            Cart newCart = Cart.builder()
                    .userId(userId)
                    .build();
            Cart savedCart = cartRepository.save(newCart);
            return CartResponse.builder()
                    .cartId(savedCart.getId())
                    .items(new ArrayList<>())
                    .userId(userId)
                    .totalAmount(0L)
                    .createdAt(savedCart.getCreatedAt())
                    .updatedAt(savedCart.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            throw new CartCreationException("Failed to create cart, error :" + e.getMessage());
        }
    }

    @Override
    public CartResponse getCartByUserId(UUID userId) {
        return null;
    }

    @Override
    public CartAddItemResponse addItemToCart(UUID userId, AddItemToCartRequest request) throws CartItemOperationException {
        try {
            Cart cart = null;
            if (!cartRepository.existsByUserId(userId)) {
                Cart newCart = Cart.builder()
                        .userId(userId)
                        .build();
                cart = cartRepository.save(newCart);
            }else cart = cartRepository.findByUserId(userId);

           CartItem cartItem = CartItem.builder()
                           .productId(request.getProductId())
                           .cart(cart)
                           .quantity(request.getQuantity())
                           .priceSnapShot(request.getPriceSnapShot())
                           .build();
           cartItemRepository.save(cartItem);

            return CartAddItemResponse.builder()
                    .success(true)
                    .message("Given Item is success fully added to the cart")
                    .build();
        }catch (Exception e){
            throw new CartItemOperationException("Failed to add item in cart, error :"+e.getMessage());
        }
    }

    @Override
    public CartResponse updateCartItemQuantity(UUID userId, UpdateCartItemQuantityRequest request) {
        try {
        }catch (Exception e){

        }
        return null;
    }

    @Override
    public CartResponse removeItemFromCart(UUID userId, RemoveItemFromCartRequest request) {
        return null;
    }

    @Override
    public ClearCartResponse clearCart(UUID userId) {
        return null;
    }

    private boolean validateCartExistence(UUID userId) throws ResourceNotFoundException {
        if (cartRepository.existsByUserId(userId)) {
            throw new ResourceNotFoundException("Cart already exist for the given user");
        }
        return true;
    }

    private boolean validateCartItemExistence(UUID cartItemId) throws ResourceNotFoundException {
        if (cartItemRepository.existsById(cartItemId)) {
            throw new ResourceNotFoundException("Cart item does not exist");
        }
        return true;
    }
}
