package com.postsproject.configuration.mock.controller;

import com.postsproject.controller.PostController;
import com.postsproject.service.blg.PostServiceImpl;
import com.postsproject.service.blg.TagServiceImpl;
import com.postsproject.service.blg.interfaces.PostService;
import com.postsproject.service.blg.interfaces.TagService;
import com.postsproject.service.util.validator.RequestPostValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import(PostController.class)
public class PostControllerUnitMockConfiguration {
    @Bean
    public PostService postService() {
        return mock(PostServiceImpl.class);
    }

    @Bean
    public TagService tagService() {
        return mock(TagServiceImpl.class);
    }

    @Bean
    public RequestPostValidator requestPostValidator() {
        return mock(RequestPostValidator.class);
    }
}
