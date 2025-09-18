package com.postsproject.configuration.mock.controller;

import com.postsproject.controller.CommentController;
import com.postsproject.service.blg.CommentServiceImpl;
import com.postsproject.service.blg.interfaces.CommentService;
import com.postsproject.service.util.validator.RequestCommentTextValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;


@Configuration
@Import(CommentController.class)
public class CommentControllerUnitMockConfiguration {
    @Bean
    protected CommentService commentService() {
        return mock(CommentServiceImpl.class);
    }

    @Bean
    protected RequestCommentTextValidator commentTextValidator() {
        return mock(RequestCommentTextValidator.class);
    }
}
