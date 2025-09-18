package com.postsproject.repository;

import com.postsproject.model.Tag;
import com.postsproject.repository.interfaces.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TagRepositoryImpl implements TagRepository  {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Tag> rowMapper = (rs, rowNum) -> {
        Tag tag = new Tag();
        tag.setId(rs.getLong("id"));
        tag.setName(rs.getString("tag_name"));
        return tag;
    };

    @Autowired
    public TagRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Optional<Tag> findByName(String name) {
        String SQL = "SELECT * FROM Tags WHERE tag_name=?";
        return jdbcTemplate.query(SQL, rowMapper, name).stream().findFirst();
    }

    @Override
    public Iterable<Tag> findTagsByPostId(Long id) {
        String SQL = """
                     SELECT Tags.* FROM Tags
                     JOIN Tags_Posts ON Tags.id=Tags_Posts.tag_id 
                     JOIN Posts on Posts.id=Tags_Posts.post_id 
                     WHERE Posts.id=?""";

        return jdbcTemplate.query(SQL, rowMapper, id);
    }

    @Override
    public void addTagToPostByTagIdAndPostId(Long tagId, Long postId) {
        String SQL = "INSERT INTO Tags_Posts(tag_id, post_id) VALUES(?,?)";

        jdbcTemplate.update(SQL, tagId, postId);
    }

    @Override
    public void removeTagFromPostByTagIdAndPostId(Long tagId, Long postId) {
        String SQL = "DELETE FROM Tags_Posts WHERE tag_id=? AND post_id=?";

        jdbcTemplate.update(SQL, tagId, postId);
    }

    @Override
    public Long getTagRelationsCountByTagId(Long tagId) {
        String SQL = "SELECT COUNT(*) FROM Tags JOIN Tags_Posts ON Tags.id=Tags_Posts.tag_id WHERE Tags.id=?";

        return jdbcTemplate.queryForObject(SQL, Long.class, tagId);
    }

    @Override
    public Tag save(Tag entity) {
        String SQL = "INSERT INTO Tags(tag_name) VALUES(?)";
        jdbcTemplate.update(SQL, entity.getName());

        return jdbcTemplate.queryForObject("SELECT * FROM Tags ORDER BY id DESC LIMIT 1", rowMapper);
    }

    @Override
    public Tag update(Tag entity) {
        String SQL = "UPDATE Tags SET tag_name=? WHERE id=?";

        jdbcTemplate.update(SQL, entity.getName(), entity.getId());

        return jdbcTemplate.queryForObject("SELECT * FROM Tags WHERE id=?", rowMapper, entity.getId());
    }

    @Override
    public Optional<Tag> findById(Long id) {
        String SQL = "SELECT * FROM Tags WHERE id=?";

        return jdbcTemplate.query(SQL, rowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public void remove(Long id) {
        String SQL = "DELETE FROM Tags WHERE id=?";
        jdbcTemplate.update(SQL, id);
    }
}
