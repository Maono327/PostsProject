package com.postsproject.integration.service.blg;

import com.postsproject.configuration.ServiceTestConfiguration;
import com.postsproject.integration.BaseRepositoryTestFiller;
import com.postsproject.model.Comment;
import com.postsproject.repository.CommentRepositoryImpl;
import com.postsproject.service.blg.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextHierarchy({
        @ContextConfiguration(name = "service", classes = ServiceTestConfiguration.class)
})
public class CommentServiceImplTest extends BaseRepositoryTestFiller {

    @Autowired
    protected CommentServiceImpl commentService;

    @Autowired
    protected CommentRepositoryImpl commentRepository;

    @BeforeEach
    protected void setUp() {
        setUpPosts();
        setUpComments();
    }

    @Test
    public void testSave() {
        Comment comment = new Comment(null, 1L, "Comment 1 text");
        Comment result =  commentService.save(comment);

        Comment expected = new Comment(7L, 1L, "Comment 1 text");

        assertEquals(expected, result);
        assertEquals(Optional.of(expected), commentRepository.findById(7L));
    }

    @Test
    public void testUpdate_success() {
        Comment comment = commentService.findCommentById(1L);
        comment.setText("New comment 1 text");
        Comment result = commentService.update(comment);

        Comment expected = new Comment(1L, 1L, "New comment 1 text");

        assertEquals(expected, result);
        assertEquals(Optional.of(expected), commentRepository.findById(1L));
    }

    @Test
    public void testUpdate_throwNoSuchElementException() {
        Comment comment = new Comment(9999L, 1L, "New comment 9999 text");

        assertThrows(NoSuchElementException.class, () -> commentService.update(comment));
    }

    @Test
    public void testRemove() {
        assertNotNull(commentService.findCommentById(1L));
        commentService.remove(1L);
        assertNull(commentService.findCommentById(1L));
    }

    @Test
    public void testFindCommentsByPostId() {
        List<Comment> expected = List.of(
                new Comment(1L, 1L, "Comment 1 text"),
                new Comment(2L, 1L, "Comment 2 text"),
                new Comment(3L, 1L, "Comment 3 text")
        );

        List<Comment> result = commentService.findCommetsByPostId(1L);
        assertEquals(expected ,result);
    }

    @Test
    public void testFindCommentById() {
        Comment expected = new Comment(1L, 1L, "Comment 1 text");
        assertEquals(expected, commentService.findCommentById(1L));
    }
}
