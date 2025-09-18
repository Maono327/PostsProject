package com.postsproject.service.util.validator;

import com.postsproject.service.util.validator.interfaces.Validator;
import org.springframework.stereotype.Component;

@Component
public class RequestCommentTextValidator implements Validator<String> {

    @Override
    public void validate(String entity) {
        if (entity == null || entity.isEmpty() || entity.length() > 1024) throw new IllegalArgumentException();
    }
}
