package com.postsproject.integration.util;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class BaseCommonTestFiller extends TestDataFiller{
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @PostConstruct
    protected void postConstruct() {
        setJdbcTemplate(jdbcTemplate);
    }

    @AfterEach()
    protected void cleanUp() {
        jdbcTemplate.execute("""
        DELETE FROM Tags;
        DELETE FROM Tags_Posts;
        DELETE FROM Comments;
        DELETE FROM Posts;
        
        ALTER TABLE Tags ALTER COLUMN id RESTART WITH 1;
        ALTER TABLE Comments ALTER COLUMN id RESTART WITH 1;
        ALTER TABLE Posts ALTER COLUMN id RESTART WITH 1;
        """);
    }
}
