package com.postsproject.dto.response;

public record ResponsePostEditFormDTO(Long id,
                                      String name,
                                      String postImage,
                                      String text,
                                      String tags) {
}
