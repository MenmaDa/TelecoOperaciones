package com.teleco.operaciones.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class ArchivosConfig implements WebMvcConfigurer {

    @Value("${app.upload.perfiles-dir:uploads/perfiles}")
    private String perfilesDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String rutaCarpeta = Path.of(perfilesDir)
                .toAbsolutePath()
                .toUri()
                .toString();

        registry.addResourceHandler("/uploads/perfiles/**")
                .addResourceLocations(rutaCarpeta);
    }
}