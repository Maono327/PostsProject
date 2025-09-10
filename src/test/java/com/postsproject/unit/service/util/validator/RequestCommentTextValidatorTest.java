package com.postsproject.unit.service.util.validator;

import com.postsproject.service.util.validator.RequestCommentTextValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestCommentTextValidatorTest {

    private final RequestCommentTextValidator commentTextValidator = new RequestCommentTextValidator();

    @Test
    public void testValidate_success() {
        assertDoesNotThrow(() -> commentTextValidator.validate("Some cool comment text"));
    }

    @Test
    public void testValidate_nullText() {
        validateThrows(null);
    }

    @Test
    public void testValidate_emptyText() {
        validateThrows("");
    }

    @Test
    public void testValidate_moreThan1024charsText() {
        validateThrows("t".repeat(1025));
    }

    protected void validateThrows(String text) {
        assertThrows(IllegalArgumentException.class, () -> commentTextValidator.validate(text));
    }
}