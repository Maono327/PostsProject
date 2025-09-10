package com.postsproject.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@Import({
        DataSourceConfiguration.class,
        ThymeleafConfiguration.class
})
@ComponentScan(basePackages = {
        "com.postsproject.controller",
        "com.postsproject.service",
        "com.postsproject.repository",
})
public class WebAppConfiguration {
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
