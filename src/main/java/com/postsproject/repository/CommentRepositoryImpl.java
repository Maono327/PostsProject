package com.postsproject.repository;

import com.postsproject.model.Comment;
import com.postsproject.repository.interfaces.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Comment> rowMapper = (rs, rowNum) -> {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setPostId(rs.getLong("post_id"));
        comment.setText(rs.getString("text"));
        return comment;
    };

    @Autowired
    public CommentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Iterable<Comment> findCommentsByPostId(Long id) {
        String SQL = "SELECT * FROM Comments WHERE post_id=?";
        return jdbcTemplate.query(SQL, rowMapper, id);
    }

    @Override
    public Comment save(Comment entity) {
        String SQL = "INSERT INTO Comments(post_id, text) VALUES(?,?)";

        jdbcTemplate.update(SQL, entity.getPostId(), entity.getText());
        return jdbcTemplate.queryForObject("SELECT * FROM Comments ORDER BY id DESC LIMIT 1", rowMapper);
    }

    @Override
    public Comment update(Comment entity) {
        String SQL = "UPDATE Comments SET text=? WHERE id=?";

        jdbcTemplate.update(SQL, entity.getText(), entity.getId());

        return jdbcTemplate.queryForObject("SELECT * FROM Comments WHERE id=?", rowMapper, entity.getId());
    }

    @Override
    public Optional<Comment> findById(Long id) {
        String SQL = "SELECT * FROM Comments WHERE id=?";
        return jdbcTemplate.query(SQL, rowMapper, id).stream().findFirst();
    }

    @Override
    public void remove(Long id) {
        String SQL = "DELETE FROM Comments WHERE id=?";
        jdbcTemplate.update(SQL, id);
    }
}
