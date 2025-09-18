package com.postsproject.service.blg.interfaces;

import com.postsproject.model.Post;
import com.postsproject.model.Tag;

public interface TagService {

    void parseAndSaveTagsForPost(String tags, Post post);

    void parseAndUpdateTagsForPost(String tags, Post post);

    Iterable<Tag> findTagsByPostId(Long id);
}
