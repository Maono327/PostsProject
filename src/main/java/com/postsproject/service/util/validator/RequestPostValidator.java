package com.postsproject.service.util.validator;

import com.postsproject.dto.request.RequestPostDTO;
import com.postsproject.service.util.validator.interfaces.Validator;
import org.springframework.stereotype.Component;

@Component
public class RequestPostValidator implements Validator<RequestPostDTO> {

    @Override
    public void validate(RequestPostDTO entity) {
        if (entity.name() == null || entity.name().isEmpty() || entity.name().length() > 256)
            throw new IllegalArgumentException();
        if (entity.image() == null)
            throw new IllegalArgumentException();
        if (entity.text() == null || entity.text().isEmpty() || entity.text().length() > 4096)
            throw new IllegalArgumentException();
    }
}
