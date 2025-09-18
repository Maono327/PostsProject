package com.postsproject.unit.service.blg;

import com.postsproject.configuration.mock.service.TagServiceUnitMockConfiguration;
import com.postsproject.model.Post;
import com.postsproject.model.PostImage;
import com.postsproject.model.Tag;
import com.postsproject.repository.interfaces.TagRepository;
import com.postsproject.service.blg.interfaces.TagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(TagServiceUnitMockConfiguration.class)
class TagServiceImplTest {
    @Autowired
    protected TagService tagService;

    @Autowired
    protected TagRepository tagRepository;

    @BeforeEach
    protected void setUp() {
        reset(tagRepository);
    }

    @Test
    protected void testParseAndSaveTagsForPost() {
        Post post = new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5);

        String tagsToSave = "Tag1 Tag2 Tag3 Tag4 Tag5 Tag6 Tag7";

        when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(tagRepository.findByName("Tag2")).thenReturn(Optional.of(new Tag(1L, "Tag2")));
        when(tagRepository.findByName("Tag5")).thenReturn(Optional.of(new Tag(2L, "Tag5")));
        when(tagRepository.findByName("Tag7")).thenReturn(Optional.of(new Tag(3L, "Tag7")));

        AtomicLong id = new AtomicLong(3L);
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocationOnMock -> {
            Tag tag = invocationOnMock.getArgument(0);
            tag.setId(id.incrementAndGet());
            return tag;
        });

        tagService.parseAndSaveTagsForPost(tagsToSave, post);

        verify(tagRepository, times(7)).findByName(anyString());
        verify(tagRepository, times(4)).save(any(Tag.class));
        verify(tagRepository, times(4)).addTagToPostByTagIdAndPostId(any(Long.class), eq(1L));
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    protected void testParseAndUpdateTagsForPost() {
        Post post = new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5);

        List<Tag> currentTags = List.of(
                new Tag(1L, "Tag1"),
                new Tag(2L, "Tag5"),
                new Tag(3L, "Tag8"),
                new Tag(4L, "Tag9"),
                new Tag(5L, "Tag10")
        );

        List<Tag> afterRemovedTags = List.of(
                new Tag(1L, "Tag1"),
                new Tag(2L, "Tag5")
        );

        String tagToSaveOrUpdate = "Tag1 Tag2 Tag3 Tag4 Tag5 Tag6 Tag7";

        when(tagRepository.findTagsByPostId(1L)).thenReturn(currentTags).thenReturn(afterRemovedTags);

        when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(tagRepository.findByName("Tag1")).thenReturn(Optional.of(new Tag(1L, "Tag1")));
        when(tagRepository.findByName("Tag5")).thenReturn(Optional.of(new Tag(2L, "Tag5")));


        when(tagRepository.getTagRelationsCountByTagId(any(Long.class))).thenReturn(100L);
        when(tagRepository.getTagRelationsCountByTagId(4L)).thenReturn(0L);
        when(tagRepository.getTagRelationsCountByTagId(5L)).thenReturn(0L);

        doNothing().when(tagRepository).removeTagFromPostByTagIdAndPostId(any(Long.class), eq(1L));
        doNothing().when(tagRepository).remove(any(Long.class));

        AtomicLong id = new AtomicLong(6L);
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocationOnMock -> {
            Tag tag = invocationOnMock.getArgument(0);
            tag.setId(id.incrementAndGet());
            return tag;
        });

        doNothing().when(tagRepository).addTagToPostByTagIdAndPostId(any(Long.class), eq(1L));

        tagService.parseAndUpdateTagsForPost(tagToSaveOrUpdate, post);

        verify(tagRepository, times(2)).findTagsByPostId(1L);
        verify(tagRepository, times(3)).getTagRelationsCountByTagId(any(Long.class));
        verify(tagRepository, times(3)).removeTagFromPostByTagIdAndPostId(any(Long.class), eq(1L));
        verify(tagRepository, times(2)).remove(any(Long.class));
        verify(tagRepository, times(7)).findByName(anyString());
        verify(tagRepository, times(5)).save(any(Tag.class));
        verify(tagRepository, times(5)).addTagToPostByTagIdAndPostId(any(Long.class), eq(1L));
        verifyNoMoreInteractions(tagRepository);
    }

    @Test
    public void testFindTagsByPostId() {
        List<Tag> tags = List.of(
                new Tag(1L, "Tag1"),
                new Tag(1L, "Tag2"),
                new Tag(1L, "Tag3"),
                new Tag(1L, "Tag4")
        );

        when(tagRepository.findTagsByPostId(1L)).thenReturn(tags);

        List<Tag> expected = List.of(
                new Tag(1L, "Tag1"),
                new Tag(1L, "Tag2"),
                new Tag(1L, "Tag3"),
                new Tag(1L, "Tag4")
        );

        Assertions.assertEquals(expected, tagService.findTagsByPostId(1L));
        verify(tagRepository, times(1)).findTagsByPostId(1L);
        verifyNoMoreInteractions(tagRepository);
    }

}