package dev.Tejveer.EcomProductService.Controller;

import dev.Tejveer.EcomProductService.DTO.Category.CategoryCreateRequest;
import dev.Tejveer.EcomProductService.DTO.Category.CategoryResponse;
import dev.Tejveer.EcomProductService.DTO.Category.CategoryTreeResponse;
import dev.Tejveer.EcomProductService.DTO.Category.CategoryUpdateRequest;
import dev.Tejveer.EcomProductService.Services.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

@RestController
@Slf4j
@RequestMapping("/apis/category")
public class CategoryController {

    private static final String ERROR_MESSAGE = "Something went wrong";

    @Autowired
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryCreateRequest request) {
        try {
            CategoryResponse response = categoryService.addCategory(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while adding new category , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(@RequestBody CategoryUpdateRequest request) {
        try {
            CategoryResponse response = categoryService.updateCategory(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while updating category , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable String categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok("Successfully deleted category, categoryId :" + categoryId);
        } catch (Exception e) {
            log.error("Error occurred while adding new category , error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable String categoryId) {
        try {
            CategoryResponse response = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while getting category :{}, error :: {}", categoryId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @GetMapping("/parent-categories")
    public ResponseEntity<List<CategoryResponse>> getParentCategories() {
        try {
            List<CategoryResponse> response = categoryService.getAllParentCategories();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while getting the parent categories, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @GetMapping("/{parentId}/immediate-sub-categories")
    public ResponseEntity<List<CategoryResponse>> getImmediateSubCategories(@PathVariable String parentCategoryId) {
        try {
            List<CategoryResponse> response = categoryService.getImmediateSubCategory(parentCategoryId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while getting the sub categories for parent category :{}, error :: {}", parentCategoryId
                    , e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    @GetMapping("/category/tree")
    public ResponseEntity<List<CategoryTreeResponse>> getCategoryTree(Pageable pageable) {
        try {
            List<CategoryTreeResponse> categoryTree = categoryService.getCategoryTree(pageable);
            return ResponseEntity.ok(categoryTree);
        } catch (Exception e) {
            log.error("Error occurred while getting the categories tree, error :: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }
}
