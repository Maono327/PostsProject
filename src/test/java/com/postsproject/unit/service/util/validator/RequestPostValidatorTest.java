package com.postsproject.unit.service.util.validator;

import com.postsproject.dto.request.RequestPostDTO;
import com.postsproject.service.util.validator.RequestPostValidator;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestPostValidatorTest {

    private final RequestPostValidator requestPostValidator = new RequestPostValidator();

    protected RequestPostDTO postDTO;

    @Test
    public void testValidate_success() {
        postDTO = new RequestPostDTO(
                "Post 1",
                "Tag1 Tag2 Tag3",
                new MockMultipartFile(
                        "image",
                        "image.png",
                        "image/png",
                        new byte[]{1}
                ),
                "Some cool text"
        );
        assertDoesNotThrow(() -> requestPostValidator.validate(postDTO));
    }

    @Test
    public void testValidate_nullName() {
        postDTO = new RequestPostDTO(
                null,
                "Tag1 Tag2 Tag3",
                new MockMultipartFile(
                        "image",
                        "image.png",
                        "image/png",
                        new byte[]{1}
                ),
                "Some cool text"
        );
        validateThrows(postDTO);
    }

    @Test
    public void testValidate_emptyName() {
        postDTO = new RequestPostDTO(
                "",
                "Tag1 Tag2 Tag3",
                new MockMultipartFile(
                        "image",
                        "image.png",
                        "image/png",
                        new byte[]{1}
                ),
                "Some cool text"
        );
        validateThrows(postDTO);
    }

    @Test
    public void testValidate_moreThan256charsName() {
        postDTO = new RequestPostDTO(
                "t".repeat(257),
                "Tag1 Tag2 Tag3",
                new MockMultipartFile(
                        "image",
                        "image.png",
                        "image/png",
                        new byte[]{1}
                ),
                "Some cool text"
        );
        validateThrows(postDTO);
    }

    @Test
    public void testValidate_nullImage() {
        postDTO = new RequestPostDTO(
                "Post 1",
                "Tag1 Tag2 Tag3",
                null,
                "Some cool text"
        );
        validateThrows(postDTO);
    }

    @Test
    public void testValidate_nullText() {
        postDTO = new RequestPostDTO(
                "Post 1",
                "Tag1 Tag2 Tag3",
                new MockMultipartFile(
                        "image",
                        "image.png",
                        "image/png",
                        new byte[]{1}
                ),
                null
        );
        validateThrows(postDTO);
    }

    @Test
    public void testValidate_emptyText() {
        postDTO = new RequestPostDTO(
                "Post 1",
                "Tag1 Tag2 Tag3",
                new MockMultipartFile(
                        "image",
                        "image.png",
                        "image/png",
                        new byte[]{1}
                ),
                ""
        );
        validateThrows(postDTO);
    }

    @Test
    public void testValidate_moreThan4096charsText() {
        postDTO = new RequestPostDTO(
                "Post 1",
                "Tag1 Tag2 Tag3",
                new MockMultipartFile(
                        "image",
                        "image.png",
                        "image/png",
                        new byte[]{1}
                ),
                "t".repeat(4097)
        );
        validateThrows(postDTO);
    }

    protected void validateThrows(RequestPostDTO dto) {
        assertThrows(IllegalArgumentException.class, () -> requestPostValidator.validate(dto));
    }
}
