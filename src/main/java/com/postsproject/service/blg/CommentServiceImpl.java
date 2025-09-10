package com.postsproject.service.blg;

import com.postsproject.model.Comment;
import com.postsproject.repository.interfaces.CommentRepository;
import com.postsproject.service.blg.interfaces.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment save(Comment comment) {
         return commentRepository.save(comment);
    }

    @Override
    public Comment update(Comment comment) {
        Comment updatable = commentRepository.findById(comment.getId()).orElseThrow(NoSuchElementException::new);
        updatable.setText(comment.getText());
        return commentRepository.update(updatable);
    }

    @Override
    public void remove(Long id) {
        commentRepository.remove(id);
    }

    @Override
    public List<Comment> findCommetsByPostId(Long id) {
        return (List<Comment>) commentRepository.findCommentsByPostId(id);
    }

    @Override
    public Comment findCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }
}
