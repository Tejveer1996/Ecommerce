package dev.tejveer.EcomCartService.service;

import dev.tejveer.EcomCartService.dto.AddItemToCartRequest;
import dev.tejveer.EcomCartService.dto.CartAddItemResponse;
import dev.tejveer.EcomCartService.dto.CartItemResponse;
import dev.tejveer.EcomCartService.dto.CartResponse;
import dev.tejveer.EcomCartService.dto.ClearCartResponse;
import dev.tejveer.EcomCartService.dto.RemoveItemFromCartRequest;
import dev.tejveer.EcomCartService.dto.UpdateCartItemQuantityRequest;
import dev.tejveer.EcomCartService.entity.Cart;
import dev.tejveer.EcomCartService.entity.CartItem;
import dev.tejveer.EcomCartService.exception.CartCreationException;
import dev.tejveer.EcomCartService.exception.CartItemOperationException;
import dev.tejveer.EcomCartService.exception.GetCartException;
import dev.tejveer.EcomCartService.exception.InventoryFeignException;
import dev.tejveer.EcomCartService.exception.ResourceNotFoundException;
import dev.tejveer.EcomCartService.repository.CartItemRepository;
import dev.tejveer.EcomCartService.repository.CartRepository;
import dev.tejveer.EcomCartService.service.clients.InventoryFeignClient;
import dev.tejveer.EcomCartService.service.clients.dto.InventoryCheckRequest;
import dev.tejveer.EcomCartService.service.clients.dto.InventoryCheckResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    InventoryFeignClient inventoryFeignClient;

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
            return this.mapFromCart(savedCart);
        } catch (Exception e) {
            throw new CartCreationException("Failed to create cart, error :" + e.getMessage());
        }
    }

    @Override
    public CartResponse getCartByUserId(UUID userId) throws GetCartException {
        try {
            if (!cartRepository.existsByUserId(userId)) {
                return this.createCart(userId);
            }
            Cart cart = cartRepository.findByUserId(userId);
            return this.mapFromCart(cart);
        } catch (Exception e) {
            throw new GetCartException("Failed to fetch cart, error :" + e.getMessage());
        }
    }

    /**
     * First validate stock from inventory
     * if cart does not exist, then it will create cart and add item to the cart with quantity one.
     * otherwise, it will check the item in the cart, if cart does not contain the item then it will
     * create the cart item and add to cart item list and if not present then create one and save.
     *
     * @param userId
     * @param request
     * @return
     * @throws CartItemOperationException
     */
    @Override
    @Transactional
    public CartAddItemResponse addItemToCart(UUID userId, AddItemToCartRequest request)
            throws CartItemOperationException {

        try {
            Cart cart = cartRepository.findByUserId(userId);
            if (cart == null) {
                cart = cartRepository.save(
                        Cart.builder()
                                .userId(userId)
                                .build()
                );
            }

            inventoryFeignClient.checkStock(InventoryCheckRequest.builder()
                    .productId(request.getProductId())
                    .requestedQuantity(1l)
                    .build());

            Optional<CartItem> optionalCartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(),
                    request.getProductId());

            if (optionalCartItem.isPresent()) {
                CartItem item = optionalCartItem.get();
                item.setQuantity(item.getQuantity() + 1);

            } else {
                cartItemRepository.save(CartItem.builder()
                        .cart(cart)
                        .productId(request.getProductId())
                        .priceSnapShot(request.getPriceSnapShot())
                        .quantity(1)
                        .build());
            }

            return CartAddItemResponse.builder()
                    .success(true)
                    .message("Item added to cart successfully.")
                    .build();

        } catch (Exception e) {
            throw new CartItemOperationException(
                    "Failed to add item to cart.", e);
        }
    }


    @Override
    public CartResponse updateCartItemQuantity(UUID userId, UpdateCartItemQuantityRequest request) throws CartItemOperationException {
        try {
            validateCartExistence(userId);
            validateCartItemExistence(request.getCartItemId());

            // Check inventory before updating the quantity in cart
            CartItem cartItem = cartItemRepository.findById(request.getCartItemId()).get();

            if (!checkStockFromInventory(cartItem.getProductId(), request.getQuantity())) {
                throw new IllegalArgumentException("Required quantity is not in stock");
            }

            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);

            Cart cart = cartRepository.findByUserId(userId);
            return this.mapFromCart(cart);
        } catch (Exception e) {
            throw new CartItemOperationException("Failed to update quantity of cart item, error :" + e.getMessage());
        }
    }

    @Override
    public CartResponse removeItemFromCart(UUID userId, RemoveItemFromCartRequest request) throws CartItemOperationException {
        try {
            validateCartExistence(userId);
            validateCartItemExistence(request.getCartItemId());
            cartItemRepository.deleteById(request.getCartItemId());
            return this.mapFromCart(cartRepository.findByUserId(userId));
        } catch (Exception e) {
            throw new CartItemOperationException("Failed to remove item from cart, error :" + e.getMessage());
        }
    }

    @Override
    public ClearCartResponse clearCart(UUID userId) throws CartItemOperationException {
        try {
            validateCartExistence(userId);
            Cart cart = cartRepository.findByUserId(userId);
            cart.setCartItemList(new ArrayList<>());
            cartRepository.save(cart);
            return ClearCartResponse.builder()
                    .cartId(cart.getId())
                    .cleared(true)
                    .message("Successfully cleared the cart, cart_id : " + cart.getId())
                    .build();
        } catch (Exception e) {
            throw new CartItemOperationException("Failed to clear cart, error :" + e.getMessage());
        }
    }

    private boolean validateCartExistence(UUID userId) throws ResourceNotFoundException {
        if (!cartRepository.existsByUserId(userId)) {
            throw new ResourceNotFoundException("Cart already exist for the given user");
        }
        return true;
    }

    private boolean validateCartItemExistence(UUID cartItemId) throws ResourceNotFoundException {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new ResourceNotFoundException("Cart item does not exist");
        }
        return true;
    }

    private CartItemResponse mapFromCartItem(CartItem cartItem) {
        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .productId(cartItem.getProductId())
                .priceSnapShot(cartItem.getPriceSnapShot())
                .quantity(cartItem.getQuantity())
                .subtotal((cartItem.getPriceSnapShot() * cartItem.getQuantity()))
                .build();
    }

    private boolean checkStockFromInventory(UUID productId, Integer requestQuantity) throws InventoryFeignException {
        try {
            InventoryCheckResponse response = inventoryFeignClient.checkStock(InventoryCheckRequest.builder()
                    .productId(productId)
                    .requestedQuantity(Long.valueOf(requestQuantity))
                    .build());
            if (response.isInStock()) {
                return true;
            }
        } catch (Exception e) {
            throw new InventoryFeignException("Failed to check stock in inventory, error :" + e.getMessage());
        }
        return false;
    }

    private CartResponse mapFromCart(Cart cart) {
        List<CartItem> cartItems = cart.getCartItemList();
        List<CartItemResponse> cartItemResponseList = cartItems.isEmpty() ? new ArrayList<>() : cartItems.stream()
                .map(this::mapFromCartItem)
                .collect(Collectors.toList());
        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(cartItemResponseList)
                .totalAmount(cartItemResponseList.isEmpty() ? 0L : cartItemResponseList.stream()
                        .map(item -> BigDecimal.valueOf(item.getPriceSnapShot())
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add).longValue()
                )
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}
