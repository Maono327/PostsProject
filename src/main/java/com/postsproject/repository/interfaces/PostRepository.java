package com.postsproject.repository.interfaces;

import com.postsproject.model.Post;
import com.postsproject.util.Page;

public interface PostRepository extends BaseRepository<Post, Long> {
    Page<Post> findAll(long pageSize, long pageNumber);
    Page<Post> findAllByTag(String searchTag, long pageSize, long pageNumber);
}
