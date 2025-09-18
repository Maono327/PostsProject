package com.postsproject.configuration.mock.service;

import com.postsproject.repository.PostRepositoryImpl;
import com.postsproject.repository.interfaces.PostRepository;
import com.postsproject.service.blg.CommentServiceImpl;
import com.postsproject.service.blg.PostServiceImpl;
import com.postsproject.service.blg.TagServiceImpl;
import com.postsproject.service.blg.interfaces.CommentService;
import com.postsproject.service.blg.interfaces.TagService;
import com.postsproject.service.util.B64Transformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import(PostServiceImpl.class)
public class PostServiceUnitMockConfiguration {
    @Bean
    public PostRepository postRepository() {
        return mock(PostRepositoryImpl.class);
    }

    @Bean
    public CommentService commentService() {
        return mock(CommentServiceImpl.class);
    }

    @Bean
    public TagService tagService() {
        return mock(TagServiceImpl.class);
    }

    @Bean
    public B64Transformer b64Transformer() {
        return mock(B64Transformer.class);
    }
}
