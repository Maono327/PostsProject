package com.postsproject.unit.service.util;

import com.postsproject.model.PostImage;
import com.postsproject.service.util.B64Transformer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class B64TransformerTest {

    private final B64Transformer transformer = new B64Transformer();

    @Test
    public void testTransform_success() {
        PostImage postImage = new PostImage("image/png", new byte[]{1,2,3,4,5,6});
        String expected = "data:image/png;base64,AQIDBAUG";

        assertEquals(expected, transformer.transform(postImage));
    }

    @Test
    public void testTransform_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> transformer.transform(null));
    }

}