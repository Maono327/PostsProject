package com.postsproject.service.blg.interfaces;

import com.postsproject.model.Comment;

import java.util.List;

public interface CommentService {

    Comment save(Comment comment);

    Comment update(Comment comment);

    void remove(Long id);

    List<Comment> findCommetsByPostId(Long id);

    Comment findCommentById(Long id);
}
