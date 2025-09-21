package com.postsproject.unit.service.blg;

import com.postsproject.model.Comment;
import com.postsproject.repository.CommentRepositoryImpl;
import com.postsproject.repository.interfaces.CommentRepository;
import com.postsproject.service.blg.CommentServiceImpl;
import com.postsproject.service.blg.interfaces.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
        CommentServiceImpl.class,
        CommentRepositoryImpl.class
})
class CommentServiceImplTest {
    @Autowired
    protected CommentService commentService;

    @MockitoBean(reset = MockReset.BEFORE)
    protected CommentRepository commentRepository;

    @Test
    protected void testSave() {
        Comment comment = new Comment(null, 1L, "Comment text");

        when(commentRepository.save(comment)).thenAnswer(invocationOnMock -> {
            Comment c = invocationOnMock.getArgument(0);
            c.setId(1L);
            return c;
        });

        Comment expected = new Comment(1L, 1L, "Comment text");

        Comment result = commentService.save(comment);
        assertEquals(expected, result);
        verify(commentRepository, times(1)).save(comment);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    protected void testUpdate_success() {
        Comment commentFromDB = new Comment(1L, 1L, "Comment text");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(commentFromDB));

        Comment updatable = new Comment(1L, 1L, "New Comment text");

        when(commentRepository.update(commentFromDB)).thenReturn(commentFromDB);

        Comment expected = new Comment(1L, 1L, "New Comment text");

        Comment result = commentService.update(updatable);

        assertEquals(expected, result);
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).update(commentFromDB);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    protected void testUpdate_throwNoSuchElementException() {
        Comment updatable = new Comment(1L, 1L, "Comment text");

        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> commentService.update(updatable));
        verify(commentRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    protected void testRemove() {
        doNothing().when(commentRepository).remove(1L);
        commentService.remove(1L);

        verify(commentRepository, times(1)).remove(1L);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    protected void testFindCommentsByPostId() {
        List<Comment> comments = List.of(
                new Comment(1L, 1L, "Comment 1 text"),
                new Comment(2L, 1L, "Comment 2 text"),
                new Comment(3L, 1L, "Comment 3 text")
        );

        when(commentRepository.findCommentsByPostId(1L)).thenReturn(comments);

        List<Comment> expected = List.of(
                new Comment(1L, 1L, "Comment 1 text"),
                new Comment(2L, 1L, "Comment 2 text"),
                new Comment(3L, 1L, "Comment 3 text")
        );

        assertEquals(expected, commentService.findCommetsByPostId(1L));
        verify(commentRepository, times(1)).findCommentsByPostId(1L);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    protected void testFindCommentById() {
        Comment comment = new Comment(1L, 1L, "Comment 1 text");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        Comment expected = new Comment(1L, 1L, "Comment 1 text");
        Comment result = commentService.findCommentById(1L);
        assertEquals(expected, result);
        verify(commentRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(commentRepository);
    }
}