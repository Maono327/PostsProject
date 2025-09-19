package com.postsproject.integration.controller;

import com.postsproject.integration.util.BaseCommonTestFiller;
import com.postsproject.model.Comment;
import com.postsproject.repository.interfaces.CommentRepository;
import com.postsproject.service.blg.interfaces.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class CommentControllerTest extends BaseCommonTestFiller {

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected CommentService commentService;

    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    protected void setUp() {
        setUpPosts();
        setUpComments();
    }

    @Test
    public void testAddCommentToPost() throws Exception {
        List<Comment> current = commentService.findCommetsByPostId(1L);
        Comment newExpected = new Comment(7L, 1L, "Comment 7 text");

        assertTrue(current.stream().filter(c -> c.equals(newExpected)).toList().isEmpty());

        mockMvc.perform(
                        post("/posts/{id}/add-comment", 1L)
                                .param("text", "Comment 7 text")
                )
                .andExpect(redirectedUrl("/posts/1"))
                .andExpect(status().is3xxRedirection());

        List<Comment> result = commentService.findCommetsByPostId(1L);

        assertFalse(result.stream().filter(c -> c.equals(newExpected)).toList().isEmpty());
    }

    @Test
    public void testEditComment() throws Exception {
        Comment current = new Comment(1L, 1L, "Comment 1 text");
        assertEquals(current, commentService.findCommentById(1L));

        mockMvc.perform(
                post("/posts/{id}/edit-comment/{commentId}", 1L, 1L)
                        .param("text", "New comment 1 text")
        )
                .andExpect(redirectedUrl("/posts/1"))
                .andExpect(status().is3xxRedirection());

        Comment expected = new Comment(1L, 1L, "New comment 1 text");

        assertEquals(expected, commentService.findCommentById(1L));
    }

    @Test
    public void testRemoveComment() throws Exception {
        assertTrue(commentRepository.findById(1L).isPresent());

        mockMvc.perform(
                post("/posts/{id}/remove/{commentId}", 1L, 1L)
        )
                .andExpect(redirectedUrl("/posts/1"))
                .andExpect(status().is3xxRedirection());

        assertTrue(commentRepository.findById(1L).isEmpty());
    }
}
