package dev.Tejveer.EcomProductService.Controller;

import dev.Tejveer.EcomProductService.DTO.Product.ProductAddRequest;
import dev.Tejveer.EcomProductService.DTO.Product.ProductBriefDto;
import dev.Tejveer.EcomProductService.DTO.Product.ProductResponse;
import dev.Tejveer.EcomProductService.DTO.Product.ProductResponseDto;
import dev.Tejveer.EcomProductService.DTO.Product.ProductUpdateRequest;
import dev.Tejveer.EcomProductService.Entity.ProductFilter;
import dev.Tejveer.EcomProductService.Services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Products APIs",
        description = "Operations related to products"
)
@RestController
@Slf4j
@RequestMapping("/apis/products")
public class ProductController {

    private final ProductService productServices;

    public ProductController(ProductService productServices) {
        this.productServices = productServices;
    }

    @Operation(summary = "Create product", description = "Add new product in the catalog")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product Created"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category Not Found", content = @Content)
    })
    @PostMapping("/add")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductAddRequest request) {
        try {
            UUID sellerId = getCurrentSellerId();
            ProductResponse response = productServices.addProduct(sellerId, request);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while adding new product, error :: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Update product", description = "Update an existing product owned by the current seller")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product Updated"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Update This Product", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content)
    })
    @PutMapping("/update")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> updateProduct(@Valid @RequestBody ProductUpdateRequest request) {
        try {
            UUID sellerId = getCurrentSellerId();
            ProductResponse response = productServices.updateProduct(request, sellerId);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while updating product, error :: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Delete product", description = "Delete a product owned by the current seller")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product Deleted"),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Delete This Product", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content)
    })
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID productId) {
        try {
            UUID sellerId = getCurrentSellerId();
            productServices.deleteProduct(productId, sellerId);
            return ResponseEntity.ok("Successfully deleted product, productId :" + productId);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while deleting product :{}, error :: {}", productId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Get product by id", description = "Fetch a single product's details using its product id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product Found"),
            @ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content)
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID productId) {
        try {
            ProductResponse response = productServices.getProductById(productId);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while getting product :{}, error :: {}", productId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Get product list by ids", description = "Fetch products details using its product ids")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product Found"),
            @ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ProductBriefDto>> getProductById(@RequestBody List<UUID> productIds) {
        try {
            return ResponseEntity.ok(productServices.getProductById(productIds));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while getting products :{}, error :: {}", productIds, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @Operation(summary = "Get all products", description = "Fetch a paginated list of all products in the catalog")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products Fetched Successfully")
    })
    @GetMapping
    public ResponseEntity<ProductResponseDto> getAllProducts(Pageable pageable) {
        try {
            ProductResponseDto response = productServices.getAllProducts(pageable);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while getting all products, error :: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Filter products", description = "Fetch a paginated list of products matching the given filter criteria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Filtered Products Fetched Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Filter Parameters", content = @Content)
    })
    @GetMapping("/filter")
    public ResponseEntity<ProductResponseDto> getFilterProducts(Pageable pageable, @RequestBody ProductFilter filter) {
        try {
            ProductResponseDto response = productServices.getFilterProducts(pageable, filter);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while filtering products, error :: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Search products", description = "Fetch a paginated list of products matching the given search keyword")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search Results Fetched Successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Search Keyword", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<ProductResponseDto> keywordSearch(Pageable pageable, @RequestParam String keyword) {
        try {
            ProductResponseDto response = productServices.keywordSearch(pageable, keyword);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while searching products with keyword :{}, error :: {}", keyword, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private UUID getCurrentSellerId() {
        try {
            String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return UUID.fromString(principal);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing seller identity");
        }
    }
}
