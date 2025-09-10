package com.postsproject.integration.repository;


import com.postsproject.integration.BaseRepositoryTestFiller;
import com.postsproject.model.Comment;
import com.postsproject.repository.CommentRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommentRepositoryImplTest extends BaseRepositoryTestFiller {

    @Autowired
    protected CommentRepositoryImpl commentRepository;

    @BeforeEach
    protected void setUp() {
        setUpPosts();
        setUpComments();
    }

    @Test
    public void testFindCommentsByIdPostId() {
        Iterable<Comment> expected1 = List.of(
                new Comment(1L, 1L, "Comment 1 text"),
                new Comment(2L, 1L, "Comment 2 text"),
                new Comment(3L, 1L, "Comment 3 text")
        );

        Iterable<Comment> expected2 = List.of(
                new Comment(4L, 2L, "Comment 4 text"),
                new Comment(5L, 2L, "Comment 5 text")
        );

        assertEquals(expected1, commentRepository.findCommentsByPostId(1L));
        assertEquals(expected2, commentRepository.findCommentsByPostId(2L));
    }

    @Test
    public void testSave() {
        Comment comment = new Comment(null, 1L, "Comment 7 text");

        Comment result = commentRepository.save(comment);
        Comment expected = new Comment(7L, 1L, "Comment 7 text");

        assertEquals(expected, result);
        assertEquals(expected, findById(expected.getId()));
    }

    @Test
    public void testUpdate() {
        Comment updatable = findById(1L);
        assertNotNull(updatable);
        updatable.setText("New comment 1 text");

        Comment result = commentRepository.update(updatable);

        Comment expected = new Comment(1L, 1L, "New comment 1 text");

        assertEquals(expected, result);
        assertEquals(expected, findById(expected.getId()));
    }

    @Test
    public void testFindById() {
        Comment expected = new Comment(5L, 2L, "Comment 5 text");
        assertEquals(Optional.of(expected), commentRepository.findById(expected.getId()));
    }

    @Test
    public void testRemove() {
        assertNotNull(findById(4L));
        commentRepository.remove(4L);
        assertNull(findById(4L));
    }

    protected static final RowMapper<Comment> rowMapper = (rs, rowNum) -> {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setPostId(rs.getLong("post_id"));
        comment.setText(rs.getString("text"));
        return comment;
    };

    protected Comment findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM Comments WHERE id=?", rowMapper, id)
                .stream()
                .findFirst()
                .orElse(null);
    }
}