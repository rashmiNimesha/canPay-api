package com.canpay.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Get the current working directory (where the JAR is located)
        String jarDir = System.getProperty("user.dir");

        // Serve images from multiple locations (filesystem and classpath)
        registry.addResourceHandler("/images/**")
                .addResourceLocations(
                        "file:" + jarDir + "/src/main/resources/static/images/",
                        "file:" + jarDir + "/static/images/",
                        "file:src/main/resources/static/images/",
                        "classpath:/static/images/")
                .setCachePeriod(3600); // Cache for 1 hour

        // Serve documents from multiple locations (filesystem and classpath)
        registry.addResourceHandler("/documents/**")
                .addResourceLocations(
                        "file:" + jarDir + "/src/main/resources/static/documents/",
                        "file:" + jarDir + "/static/documents/",
                        "file:src/main/resources/static/documents/",
                        "classpath:/static/documents/")
                .setCachePeriod(3600); // Cache for 1 hour
    }
}