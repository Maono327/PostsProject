package com.postsproject.integration;

import com.postsproject.configuration.RepositoryTestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextHierarchy(
        @ContextConfiguration(name = "repo", classes = RepositoryTestConfiguration.class)
)
@TestPropertySource("classpath:test-database-application.properties")
public class BaseRepositoryTestFiller {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

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

    protected void setUpPosts() {
        String SQL =
                "INSERT INTO Posts(post_name, image_content_type, image_payload, text, likes_count) VALUES(?,?,?,?,?)";
        jdbcTemplate.update(SQL, "Post 1 name", "image/png", new byte[]{1}, "Post 1 text", 5);
        jdbcTemplate.update(SQL, "Post 2 name", "image/png", new byte[]{2}, "Post 2 text", 10);
        jdbcTemplate.update(SQL, "Post 3 name", "image/png", new byte[]{3}, "Post 3 text", 15);
        jdbcTemplate.update(SQL, "Post 4 name", "image/png", new byte[]{4}, "Post 4 text", 20);
        jdbcTemplate.update(SQL, "Post 5 name", "image/png", new byte[]{5}, "Post 5 text", 25);
    }

    protected void setUpTags() {
        String tagsSQL =
                "INSERT INTO Tags(tag_name) VALUES(?)";

        jdbcTemplate.update(tagsSQL, "Tag1");
        jdbcTemplate.update(tagsSQL, "Tag2");
        jdbcTemplate.update(tagsSQL, "Tag3");
        jdbcTemplate.update(tagsSQL, "Tag4");
        jdbcTemplate.update(tagsSQL, "Tag5");

        String tagsPostsSQL = "INSERT INTO Tags_Posts(tag_id, post_id) VALUES(?,?)";

        jdbcTemplate.update(tagsPostsSQL, 1L, 1L);
        jdbcTemplate.update(tagsPostsSQL, 1L, 3L);
        jdbcTemplate.update(tagsPostsSQL, 1L, 4L);
        jdbcTemplate.update(tagsPostsSQL, 1L, 5L);

        jdbcTemplate.update(tagsPostsSQL, 2L, 1L);
        jdbcTemplate.update(tagsPostsSQL, 2L, 2L);

        jdbcTemplate.update(tagsPostsSQL, 3L, 3L);

        jdbcTemplate.update(tagsPostsSQL, 4L, 4L);
    }

    protected void setUpComments() {
        String SQL = "INSERT INTO Comments(post_id, text) VALUES (?,?)";

        jdbcTemplate.update(SQL, 1, "Comment 1 text");
        jdbcTemplate.update(SQL, 1, "Comment 2 text");
        jdbcTemplate.update(SQL, 1, "Comment 3 text");
        jdbcTemplate.update(SQL, 2, "Comment 4 text");
        jdbcTemplate.update(SQL, 2, "Comment 5 text");
        jdbcTemplate.update(SQL, 3, "Comment 6 text");
    }
}
