package com.postsproject.service.util;

import com.postsproject.model.PostImage;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class B64Transformer {
    public String transform(PostImage postImage) {
        if (postImage == null) throw new IllegalArgumentException();
        String b64ImageBuilder = "data:" +
                postImage.getContentType() +
                ";base64," +
                Base64.getEncoder().encodeToString(postImage.getPayload());
        return b64ImageBuilder;
    }
}
