package dev.Tejveer.EcomProductService.Controller;

import dev.Tejveer.EcomProductService.DTO.Category.CategoryCreateRequest;
import dev.Tejveer.EcomProductService.DTO.Category.CategoryResponse;
import dev.Tejveer.EcomProductService.DTO.Category.CategoryTreeResponse;
import dev.Tejveer.EcomProductService.DTO.Category.CategoryUpdateRequest;
import dev.Tejveer.EcomProductService.Exception.ResourceNotFoundException;
import dev.Tejveer.EcomProductService.Services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(
        name = "Category APIs",
        description = "Operations related to product categories"
)
@RestController
@Slf4j
@EnableMethodSecurity
@RequestMapping("/apis/category")
public class CategoryController {

    @Autowired
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Create category", description = "Add a new category to the catalog (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category Created"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Create Category", content = @Content),
            @ApiResponse(responseCode = "404", description = "Invalid Parent Category Id", content = @Content)
    })
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryCreateRequest request) {
        try {
            CategoryResponse response = categoryService.addCategory(request);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while adding new category , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Update category", description = "Update an existing category (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category Updated"),
            @ApiResponse(responseCode = "400", description = "Validation Failed", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Update Category", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category Not Found", content = @Content)
    })
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(@RequestBody CategoryUpdateRequest request) throws ResourceNotFoundException {
        try {
            CategoryResponse response = categoryService.updateCategory(request);
            return response;
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while updating category , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Delete category", description = "Delete a category from the catalog (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category Deleted"),
            @ApiResponse(responseCode = "403", description = "Not Authorized To Delete Category", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category Not Found", content = @Content)
    })
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable String categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok("Successfully deleted category, categoryId :" + categoryId);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while adding new category , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Get category by id", description = "Fetch a single category's details using its category id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category Found"),
            @ApiResponse(responseCode = "404", description = "Category Not Found", content = @Content)
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable String categoryId) {
        try {
            CategoryResponse response = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while getting category :{}, error :: {}", categoryId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Get parent categories", description = "Fetch all top-level parent categories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parent Categories Fetched Successfully")
    })
    @GetMapping("/parent-categories")
    public ResponseEntity<List<CategoryResponse>> getParentCategories() {
        try {
            List<CategoryResponse> response = categoryService.getAllParentCategories();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while getting the parent categories, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Get immediate sub-categories", description = "Fetch the direct child categories of a given parent category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sub-Categories Fetched Successfully (empty list if parent has none, or parent id does not exist)")
    })
    @GetMapping("/{parentCategoryId}/immediate-sub-categories")
    public ResponseEntity<List<CategoryResponse>> getImmediateSubCategories(@PathVariable String parentCategoryId) {
        try {
            List<CategoryResponse> response = categoryService.getImmediateSubCategory(parentCategoryId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while getting the sub categories for parent category :{}, error :: {}", parentCategoryId
                    , e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Operation(summary = "Get category tree", description = "Fetch a paginated hierarchical tree of all categories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category Tree Fetched Successfully")
    })
    @GetMapping("/tree")
    public ResponseEntity<List<CategoryTreeResponse>> getCategoryTree(Pageable pageable) {
        try {
            List<CategoryTreeResponse> categoryTree = categoryService.getCategoryTree(pageable);
            return ResponseEntity.ok(categoryTree);
        } catch (Exception e) {
            log.error("Error occurred while getting the categories tree, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
