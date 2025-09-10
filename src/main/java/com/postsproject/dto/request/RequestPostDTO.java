package com.postsproject.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record RequestPostDTO(
        String name,
        String tags,
        MultipartFile image,
        String text) {
}
