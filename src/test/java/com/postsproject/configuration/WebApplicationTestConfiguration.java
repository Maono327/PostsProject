package com.postsproject.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@Import(ThymeleafConfiguration.class)
@ComponentScan("com.postsproject.controller")
@EnableWebMvc
public class WebApplicationTestConfiguration {
}
