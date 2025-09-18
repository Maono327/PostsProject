package com.postsproject.repository.interfaces;

import com.postsproject.model.Comment;

public interface CommentRepository extends BaseRepository<Comment, Long> {
    Iterable<Comment> findCommentsByPostId(Long id);
}
