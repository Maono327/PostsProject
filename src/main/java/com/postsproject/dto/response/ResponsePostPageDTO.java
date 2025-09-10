package com.postsproject.dto.response;

import com.postsproject.model.Comment;
import com.postsproject.model.Tag;

import java.util.List;

public record ResponsePostPageDTO(Long id,
                                  String name,
                                  String postImage,
                                  String text,
                                  List<Tag> tags,
                                  Long likesCount,
                                  List<Comment> comments) {
}
