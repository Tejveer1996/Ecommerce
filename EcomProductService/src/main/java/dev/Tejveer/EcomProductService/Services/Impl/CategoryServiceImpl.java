package dev.Tejveer.EcomProductService.Services.Impl;

import dev.Tejveer.EcomProductService.DTO.Category.CategoryCreateRequest;
import dev.Tejveer.EcomProductService.DTO.Category.CategoryResponse;
import dev.Tejveer.EcomProductService.DTO.Category.CategoryTreeResponse;
import dev.Tejveer.EcomProductService.DTO.Category.CategoryUpdateRequest;
import dev.Tejveer.EcomProductService.Entity.Category;
import dev.Tejveer.EcomProductService.Exception.ResourceNotFoundException;
import dev.Tejveer.EcomProductService.Repository.CategoryRepository;
import dev.Tejveer.EcomProductService.Services.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;
    ModelMapper modelMapper;

    @Override
    public CategoryResponse getCategoryById(String categoryId) throws ResourceNotFoundException {
        Category category = categoryRepository.findById(UUID.fromString(categoryId)).orElseThrow(
                () -> new ResourceNotFoundException("Invalid category id")
        );
        return modelMapper.map(category, CategoryResponse.class);
    }

    /**
     * Parent category can be null as if the given category could be the parent category
     *
     * @param createRequest
     * @return
     */
    @Override
    public CategoryResponse addCategory(CategoryCreateRequest createRequest) throws ResourceNotFoundException {
        if (createRequest.getParentId() != null &&
                !categoryRepository.existsById(UUID.fromString(createRequest.getParentId()))) {
            throw new ResourceNotFoundException("Invalid parent id");
        }
        Category category = modelMapper.map(createRequest, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryResponse.class);
    }

    @Override
    public CategoryResponse updateCategory(CategoryUpdateRequest updateRequest) throws ResourceNotFoundException {
        Category category = categoryRepository.findById(UUID.fromString(updateRequest.getId())).orElseThrow(
                () -> new ResourceNotFoundException("Invalid category id")
        );
        if (updateRequest.getName() != null) {
            category.setName(updateRequest.getName());
        }
        if (updateRequest.getDescription() != null) {
            category.setDescription(updateRequest.getDescription());
        }
        Category updatedCategory = categoryRepository.save(category);
        return modelMapper.map(updatedCategory, CategoryResponse.class);
    }

    @Override
    public boolean deleteCategory(String categoryId) throws ResourceNotFoundException {
        Category category = categoryRepository.findById(UUID.fromString(categoryId)).orElseThrow(
                () -> new ResourceNotFoundException("Invalid category id")
        );
        if (category.getParentCategoryId() == null) {
            throw new ResourceNotFoundException("Parent category cant be delete");
        }
        categoryRepository.deleteById(UUID.fromString(categoryId));
        return true;
    }

    @Override
    public List<CategoryResponse> getAllParentCategories() {
        List<Category> parentCategories = categoryRepository.findByParentCategoryIdIsNull();
        return parentCategories.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getImmediateSubCategory(String parentCategoryId) {
        List<Category> categories =
                categoryRepository.findByParentCategoryId(UUID.fromString(parentCategoryId));

        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .toList();

    }


    @Override
    public List<CategoryTreeResponse> getCategoryTree(Pageable pageable) {
        List<Category> rootCategories = categoryRepository.findByParentCategoryIdIsNull(pageable);

        List<CategoryTreeResponse> response = new ArrayList<>();

        for (Category category : rootCategories) {
            response.add(buildCategoryTree(category));
        }
        return response;
    }

    private CategoryTreeResponse buildCategoryTree(Category category) {
        CategoryTreeResponse response = CategoryTreeResponse.builder()
                .id(category.getId().toString())
                .name(category.getName())
                .build();
        List<Category> childCategories = categoryRepository.findByParentCategoryId(category.getId());
        List<CategoryTreeResponse> children = new ArrayList<>();
        for (Category child : childCategories) {
            children.add(buildCategoryTree(child));
        }
        response.setChildren(children);
        return response;
    }

}
