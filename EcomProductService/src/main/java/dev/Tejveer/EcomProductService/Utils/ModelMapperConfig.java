package dev.Tejveer.EcomProductService.Utils;

import dev.Tejveer.EcomProductService.DTO.Product.ProductResponse;
import dev.Tejveer.EcomProductService.Entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.typeMap(Product.class, ProductResponse.class).addMappings(m -> {
            m.map(src -> src.getCategory().getId(), ProductResponse::setCategoryId);
            m.map(src -> src.getCategory().getName(), ProductResponse::setCategoryName);
        });

        return mapper;
    }
}
