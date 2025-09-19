package com.postsproject.integration.service.blg;

import com.postsproject.integration.util.BaseCommonTestFiller;
import com.postsproject.model.Post;
import com.postsproject.model.Tag;
import com.postsproject.repository.TagRepositoryImpl;
import com.postsproject.service.blg.TagServiceImpl;
import com.postsproject.service.blg.interfaces.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TagServiceImplTest extends BaseCommonTestFiller {
    @Autowired
    protected TagServiceImpl tagService;

    @Autowired
    protected TagRepositoryImpl tagRepository;

    @Autowired
    private PostService postService;

    @BeforeEach
    protected void setUp() {
        setUpPosts();
        setUpTags();
    }

    @Test
    public void testParseAndSaveTagsForPost() {
        String tags = "Tag1 Tag2 Tag6 Tag7 Tag8";
        Post post = postService.findPostById(1L);

        List<Tag> currentExpected = List.of(new Tag(1L, "Tag1"), new Tag(2L, "Tag2"));
        assertEquals(currentExpected, tagService.findTagsByPostId(1L));

        tagService.parseAndSaveTagsForPost(tags, post);

        List<Tag> resultExpected = List.of(
                new Tag(1L, "Tag1"),
                new Tag(2L, "Tag2"),
                new Tag(6L, "Tag6"),
                new Tag(7L, "Tag7"),
                new Tag(8L, "Tag8"));
        assertEquals(resultExpected, tagService.findTagsByPostId(1L));
    }

    @Test
    public void testParseAndUpdateTagsForPost() {
        String tags = "Tag6 Tag7";
        Post post = postService.findPostById(3L);

        List<Tag> currentExpected = List.of(new Tag(1L, "Tag1"), new Tag(3L, "Tag3"));
        assertEquals(currentExpected, tagService.findTagsByPostId(3L));

        tagService.parseAndUpdateTagsForPost(tags, post);

        List<Tag> resultExpected = List.of(
                new Tag(6L, "Tag6"),
                new Tag(7L, "Tag7")
        );
        assertEquals(resultExpected, tagService.findTagsByPostId(3L));
        assertTrue(tagRepository.findById(1L).isPresent());
        assertTrue(tagRepository.findById(3L).isEmpty());
    }

    @Test
    public void testFindTagsByPostId() {
        List<Tag> expected = List.of(
            new Tag(1L, "Tag1"),
            new Tag(2L, "Tag2")
        );

        assertEquals(expected, tagService.findTagsByPostId(1L));
    }
}
