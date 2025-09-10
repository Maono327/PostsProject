package com.postsproject.integration.repository;

import com.postsproject.integration.BaseRepositoryTestFiller;
import com.postsproject.model.Tag;
import com.postsproject.repository.TagRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TagRepositoryImplTest extends BaseRepositoryTestFiller {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected TagRepositoryImpl tagRepository;

    @BeforeEach
    protected void setUp() {
        setUpPosts();
        setUpTags();
    }

    @Test
    public void testFindByName() {
        Tag expected = new Tag(3L, "Tag3");
        assertEquals(Optional.of(expected), tagRepository.findByName(expected.getName()));
    }

    @Test
    public void testFindTagsByPostId() {
        Iterable<Tag> expected = List.of(
                new Tag(1L, "Tag1"),
                new Tag(2L, "Tag2")
        );

        assertEquals(expected, tagRepository.findTagsByPostId(1L));
    }

    @Test
    public void testAddTagToPostByTagIdAndPostId() {
        List<Tag> post1TagsBefore = (List<Tag>) findTagsByPostId(1L);
        Tag tagToAdd = findById(3L);

        assertNull(post1TagsBefore
                .stream()
                .filter(tag -> tag.equals(tagToAdd))
                .findFirst()
                .orElse(null));

        tagRepository.addTagToPostByTagIdAndPostId(tagToAdd.getId(), 1L);

        List<Tag> post1TagsAfter = (List<Tag>) findTagsByPostId(1L);
        assertNotNull(post1TagsAfter
                .stream()
                .filter(tag -> tag.equals(tagToAdd))
                .findFirst()
                .orElse(null));
    }

    @Test
    public void testRemoveTagFromPostByTagIdAndPostId() {
        List<Tag> post1TagsBefore = (List<Tag>) findTagsByPostId(1L);
        Tag tagToRemove = findById(1L);

        assertNotNull(post1TagsBefore
                .stream()
                .filter(tag -> tag.equals(tagToRemove))
                .findFirst()
                .orElse(null));

        tagRepository.removeTagFromPostByTagIdAndPostId(1L, 1L);

        List<Tag> post1TagsAfter = (List<Tag>) findTagsByPostId(1L);
        assertNull(post1TagsAfter
                .stream()
                .filter(tag -> tag.equals(tagToRemove))
                .findFirst()
                .orElse(null));
    }

    @Test
    public void testGetTagRelationsCountByTagId() {
        long expected = 4;
        assertEquals(expected, tagRepository.getTagRelationsCountByTagId(1L));
    }

    @Test
    public void testSave() {
        Tag tag = new Tag(null, "Tag6");

        Tag expected = new Tag(6L, "Tag6");

        Tag result = tagRepository.save(tag);

        assertEquals(expected, result);
        assertEquals(expected, findById(expected.getId()));
    }

    @Test
    public void testUpdate() {
        Tag updatable = findById(1L);
        assertNotNull(updatable);
        updatable.setName("New Tag1 name");

        Tag result = tagRepository.update(updatable);

        Tag expected = new Tag(1L, "New Tag1 name");

        assertEquals(expected, result);
        assertEquals(expected, findById(1L));
    }

    @Test
    public void testRemove () {
        Tag removable = findById(1L);
        assertNotNull(removable);

        tagRepository.remove(removable.getId());

        assertNull(findById(1L));
    }

    @Test
    public void findById() {
        Tag expected = new Tag(1L, "Tag1");
        assertEquals(Optional.of(expected), tagRepository.findById(1L));
    }

    protected static final RowMapper<Tag> rowMapper = (rs, rowNum) -> {
        Tag tag = new Tag();
        tag.setId(rs.getLong("id"));
        tag.setName(rs.getString("tag_name"));
        return tag;
    };

    protected Tag findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM Tags WHERE id=?", rowMapper, id)
                .stream()
                .findFirst()
                .orElse(null);
    }

    protected Iterable<Tag> findTagsByPostId(Long postId) {
        String SQL = """
                     SELECT Tags.* FROM Tags
                     JOIN Tags_Posts ON Tags.id=Tags_Posts.tag_id 
                     JOIN Posts on Posts.id=Tags_Posts.post_id 
                     WHERE Posts.id=?""";

        return jdbcTemplate.query(SQL, rowMapper, postId);
    }
}