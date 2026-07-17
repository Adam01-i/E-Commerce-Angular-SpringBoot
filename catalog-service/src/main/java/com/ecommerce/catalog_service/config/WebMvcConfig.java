package com.ecommerce.catalog_service.config;

import com.ecommerce.catalog_service.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final ImageStorageService imageStorageService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/produits/images/**")
                .addResourceLocations("file:" + imageStorageService.getUploadDir() + "/");
    }
}
