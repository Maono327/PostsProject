package com.postsproject.unit.controller;

import com.postsproject.model.Comment;
import com.postsproject.service.blg.interfaces.CommentService;
import com.postsproject.service.util.validator.RequestCommentTextValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CommentControllerTest {

    @MockitoBean(reset = MockReset.BEFORE)
    protected CommentService commentService;

    @MockitoBean(reset = MockReset.BEFORE)
    protected RequestCommentTextValidator validator;

    @Autowired
    protected MockMvc mockMvc;

    @Test
    public void testAddCommentToPost() throws Exception {
        doNothing().when(validator).validate(anyString());
        when(commentService.save(any(Comment.class))).thenAnswer(c -> c.getArgument(0));

        mockMvc.perform(
                    post("/posts/{id}/add-comment", 1L)
                            .param("text", "Comment 1 text")
                )
                .andExpect(redirectedUrl("/posts/1"))
                .andExpect(status().is3xxRedirection());

        Comment expectedSave = new Comment(null, 1L, "Comment 1 text");
        verify(commentService, times(1)).save(expectedSave);
        verify(validator, times(1)).validate("Comment 1 text");
        verifyNoMoreInteractions(commentService);
        verifyNoMoreInteractions(validator);
    }

    @Test
    public void testEditComment() throws Exception {
        Comment commentFromDb = new Comment(1L, 1L, "Old Text");

        doNothing().when(validator).validate(anyString());
        when(commentService.findCommentById(1L)).thenReturn(commentFromDb);
        when(commentService.update(any(Comment.class))).thenAnswer(c -> c.getArgument(0));

        mockMvc.perform(
                post("/posts/{id}/edit-comment/{commentId}", 1L, 1L)
                        .param("text", "New Comment 1 text")
                        .param("_method", "update")
                )
                .andExpect(redirectedUrl("/posts/1"))
                .andExpect(status().is3xxRedirection());

        Comment expectedUpdate = new Comment(1L, 1L, "New Comment 1 text");
        verify(validator, times(1)).validate("New Comment 1 text");
        verify(commentService, times(1)).findCommentById(1L);
        verify(commentService, times(1)).update(expectedUpdate);
        verifyNoMoreInteractions(commentService);
        verifyNoMoreInteractions(validator);
    }

    @Test
    public void testRemoveComment() throws Exception {
        doNothing().when(commentService).remove(anyLong());

        mockMvc.perform(
                post("/posts/{id}/remove/{commentId}", 1L, 1L)
                        .param("_method", "delete"))
                .andExpect(redirectedUrl("/posts/1"))
                .andExpect(status().is3xxRedirection());

        verify(commentService, times(1)).remove(anyLong());
        verifyNoMoreInteractions(commentService);
        verifyNoInteractions(validator);
    }
}