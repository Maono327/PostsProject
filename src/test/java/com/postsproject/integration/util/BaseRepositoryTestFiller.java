package com.postsproject.integration.util;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@DataJdbcTest(
        includeFilters = @ComponentScan.Filter(Repository.class)
)
public class BaseRepositoryTestFiller extends TestDataFiller {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @PostConstruct
    protected void postConstruct() {
        setJdbcTemplate(jdbcTemplate);
    }

    @AfterEach()
    protected void cleanUp() {
        jdbcTemplate.execute("""
        ALTER TABLE Tags ALTER COLUMN id RESTART WITH 1;
        ALTER TABLE Comments ALTER COLUMN id RESTART WITH 1;
        ALTER TABLE Posts ALTER COLUMN id RESTART WITH 1;
        """);
    }
}
