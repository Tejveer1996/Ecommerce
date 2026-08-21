package dev.Tejveer.EcomProductService.Services;

import dev.Tejveer.EcomProductService.DTO.Category.CategoryCreateRequest;
import dev.Tejveer.EcomProductService.DTO.Category.CategoryResponse;
import dev.Tejveer.EcomProductService.DTO.Category.CategoryTreeResponse;
import dev.Tejveer.EcomProductService.DTO.Category.CategoryUpdateRequest;
import dev.Tejveer.EcomProductService.Exception.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    // Basic CRUD operation Category
    CategoryResponse getCategoryById(String categoryId) throws ResourceNotFoundException;

    CategoryResponse addCategory(CategoryCreateRequest createRequest) throws ResourceNotFoundException;

    CategoryResponse updateCategory(CategoryUpdateRequest updateRequest) throws ResourceNotFoundException;

    boolean deleteCategory(String categoryId) throws ResourceNotFoundException;

    List<CategoryTreeResponse> getCategoryTree(Pageable pageable);

    List<CategoryResponse> getAllParentCategories();

    List<CategoryResponse> getImmediateSubCategory(String parentCategoryId) throws ResourceNotFoundException;

}
