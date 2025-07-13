package com.canpay.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Serve images from classpath
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(3600); // Cache for 1 hour

        // Serve documents from classpath
        registry.addResourceHandler("/documents/**")
                .addResourceLocations("classpath:/static/documents/")
                .setCachePeriod(3600); // Cache for 1 hour
    }
}