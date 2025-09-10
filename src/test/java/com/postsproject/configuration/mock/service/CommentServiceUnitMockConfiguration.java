package com.postsproject.configuration.mock.service;

import com.postsproject.repository.CommentRepositoryImpl;
import com.postsproject.repository.interfaces.CommentRepository;
import com.postsproject.service.blg.CommentServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import(CommentServiceImpl.class)
public class CommentServiceUnitMockConfiguration {
    @Bean
    public CommentRepository commentRepository() {
        return mock(CommentRepositoryImpl.class);
    }
}
