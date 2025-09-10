package com.postsproject.repository.interfaces;

import com.postsproject.model.Tag;

import java.util.Optional;

public interface TagRepository extends BaseRepository<Tag, Long>{

    Optional<Tag> findByName(String name);

    Iterable<Tag> findTagsByPostId(Long id);

    void addTagToPostByTagIdAndPostId(Long tagId, Long postId);

    void removeTagFromPostByTagIdAndPostId(Long tagId, Long postId);

    Long getTagRelationsCountByTagId(Long tagId);

}
