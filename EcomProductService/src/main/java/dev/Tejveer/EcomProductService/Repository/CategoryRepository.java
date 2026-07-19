package dev.Tejveer.EcomProductService.Repository;

import dev.Tejveer.EcomProductService.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByParentCategoryIdIsNull();
    List<Category> findByParentCategoryId(UUID parentCategoryId);
}
