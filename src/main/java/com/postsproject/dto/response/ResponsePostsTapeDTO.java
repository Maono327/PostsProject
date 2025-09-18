package com.postsproject.dto.response;

import com.postsproject.model.Tag;

import java.util.List;

public record ResponsePostsTapeDTO(Long id,
                                   String name,
                                   String postImage,
                                   String text,
                                   List<Tag> tags,
                                   Long likesCount,
                                   Long commentsCount) {
}
