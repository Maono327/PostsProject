package com.postsproject.configuration.mock.service;

import com.postsproject.repository.interfaces.TagRepository;
import com.postsproject.service.blg.TagServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import(TagServiceImpl.class)
public class TagServiceUnitMockConfiguration {
    @Bean
    public TagRepository tagRepository() {
        return mock(TagRepository.class);
    }
}
