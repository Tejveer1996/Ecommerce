package dev.tejveer.EcomCartService.controller;

import dev.tejveer.EcomCartService.dto.AddItemToCartRequest;
import dev.tejveer.EcomCartService.dto.CartAddItemResponse;
import dev.tejveer.EcomCartService.dto.CartResponse;
import dev.tejveer.EcomCartService.dto.ClearCartResponse;
import dev.tejveer.EcomCartService.dto.RemoveItemFromCartRequest;
import dev.tejveer.EcomCartService.dto.UpdateCartItemQuantityRequest;
import dev.tejveer.EcomCartService.exception.CartCreationException;
import dev.tejveer.EcomCartService.exception.CartItemOperationException;
import dev.tejveer.EcomCartService.exception.GetCartException;
import dev.tejveer.EcomCartService.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Tag(
        name = "Cart APIs",
        description = "Operations related to the buyer's shopping cart"
)
@RestController
@Slf4j
@RequestMapping("/apis/cart")
public class CartController {

    private static final String ERROR_MESSAGE = "Something went wrong";

    @Autowired
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "Create cart", description = "Create a new cart for the logged-in user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart Created"),
            @ApiResponse(responseCode = "400", description = "Cart Already Exists / Creation Failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing Or Invalid Authentication Token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Create Cart", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> createCart() {
        try {
            UUID userId = extractUserId();
            CartResponse response = cartService.createCart(userId);
            return ResponseEntity.ok(response);
        } catch (CartCreationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while creating cart , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Get cart", description = "Fetch the logged-in user's cart along with all items")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart Found"),
            @ApiResponse(responseCode = "401", description = "Missing Or Invalid Authentication Token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized To View Cart", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cart Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> getCartByUserId() {
        try {
            UUID userId = extractUserId();
            CartResponse response = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(response);
        } catch (GetCartException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while getting cart , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Add item to cart", description = "Add a product to the logged-in user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item Added To Cart"),
            @ApiResponse(responseCode = "400", description = "Validation Failed / Operation Failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing Or Invalid Authentication Token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Modify Cart", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartAddItemResponse> addItemToCart(@RequestBody AddItemToCartRequest request) {
        try {
            UUID userId = extractUserId();
            CartAddItemResponse response = cartService.addItemToCart(userId, request);
            return ResponseEntity.ok(response);
        } catch (CartItemOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while adding item to cart , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Update cart item quantity", description = "Update the quantity of an existing item in the logged-in user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart Item Quantity Updated"),
            @ApiResponse(responseCode = "400", description = "Validation Failed / Operation Failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing Or Invalid Authentication Token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Modify Cart", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cart Item Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PutMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> updateCartItemQuantity(@RequestBody UpdateCartItemQuantityRequest request) {
        try {
            UUID userId = extractUserId();
            CartResponse response = cartService.updateCartItemQuantity(userId, request);
            return ResponseEntity.ok(response);
        } catch (CartItemOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while updating cart item quantity , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Remove item from cart", description = "Remove a specific item from the logged-in user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart Item Removed"),
            @ApiResponse(responseCode = "400", description = "Validation Failed / Operation Failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing Or Invalid Authentication Token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Modify Cart", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cart Item Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @DeleteMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> removeItemFromCart(@RequestBody RemoveItemFromCartRequest request) {
        try {
            UUID userId = extractUserId();
            CartResponse response = cartService.removeItemFromCart(userId, request);
            return ResponseEntity.ok(response);
        } catch (CartItemOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while removing item from cart , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @Operation(summary = "Clear cart", description = "Remove all items from the logged-in user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart Cleared"),
            @ApiResponse(responseCode = "400", description = "Operation Failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing Or Invalid Authentication Token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Modify Cart", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ClearCartResponse> clearCart() {
        try {
            UUID userId = extractUserId();
            ClearCartResponse response = cartService.clearCart(userId);
            return ResponseEntity.ok(response);
        } catch (CartItemOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while clearing cart , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    private UUID extractUserId() {
        return UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
