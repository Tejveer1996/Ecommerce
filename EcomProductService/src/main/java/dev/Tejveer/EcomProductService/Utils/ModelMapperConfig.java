package dev.Tejveer.EcomProductService.Utils;

import dev.Tejveer.EcomProductService.DTO.Category.CategoryCreateRequest;
import dev.Tejveer.EcomProductService.DTO.Category.CategoryResponse;
import dev.Tejveer.EcomProductService.DTO.Product.ProductResponse;
import dev.Tejveer.EcomProductService.Entity.Category;
import dev.Tejveer.EcomProductService.Entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.typeMap(Product.class, ProductResponse.class).addMappings(m -> {
            m.map(src -> src.getCategory().getId(), ProductResponse::setCategoryId);
            m.map(src -> src.getCategory().getName(), ProductResponse::setCategoryName);
        });
        mapper.typeMap(CategoryCreateRequest.class, Category.class).addMappings(m -> {
            m.skip(Category::setId);
            m.map(src -> src.getParentCategoryId() == null
                            ? null
                            : UUID.fromString(src.getParentCategoryId()),
                    Category::setParentCategoryId);
        });

        mapper.typeMap(Category.class, CategoryResponse.class).addMappings(m -> {
            m.map(src -> src.getParentCategoryId() == null
                            ? null
                            : src.getParentCategoryId().toString(),
                    CategoryResponse::setParentCategoryId);
        });

        return mapper;
    }
}
