package com.postsproject.repository;

import com.postsproject.model.Post;
import com.postsproject.model.PostImage;
import com.postsproject.repository.interfaces.PostRepository;
import com.postsproject.util.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostRepositoryImpl implements PostRepository {
    private final JdbcTemplate jdbcTemplate;

    public PostRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Post> rowMapper = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setName(rs.getString("post_name"));
        post.setPostImage(new PostImage(
                rs.getString("image_content_type"),
                rs.getBytes("image_payload")));
        post.setText(rs.getString("text"));
        post.setLikesCount(rs.getLong("likes_count"));
        return post;
    };

    @Override
    public Page<Post> findAll(long pageSize, long pageNumber) {
        String postsSQL = "SELECT * FROM Posts OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        List<Post> posts = jdbcTemplate.query(postsSQL, rowMapper, (pageNumber - 1) * pageSize, pageSize);

        String countSQL = "SELECT COUNT(*) FROM Posts";
        long total = jdbcTemplate.queryForObject(countSQL, Long.class);

        return new Page<>("", pageSize, pageNumber, (long) Math.ceil((double) total / pageSize), posts);
    }

    @Override
    public Page<Post> findAllByTag(String searchTag, long pageSize, long pageNumber) {
        String postsSQL = """
                          SELECT Posts.* FROM Posts
                          JOIN Tags_Posts ON Posts.id=Tags_Posts.post_id 
                          JOIN Tags on Tags.id=Tags_Posts.tag_id 
                          WHERE Tags.tag_name=?
                          OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                          """;

        List<Post> posts = jdbcTemplate.query(postsSQL, rowMapper,  searchTag, (pageNumber - 1) * pageSize, pageSize);

        String countSQL = """
                          SELECT COUNT(*) FROM Posts
                          JOIN Tags_Posts ON Posts.id=Tags_Posts.post_id 
                          JOIN Tags on Tags.id=Tags_Posts.tag_id 
                          WHERE Tags.tag_name=?
                          """;
        long total = jdbcTemplate.queryForObject(countSQL, Long.class, searchTag);

        return new Page<>(searchTag, pageSize, pageNumber, (long) Math.ceil((double) total / pageSize), posts);
    }

    @Override
    public Post save(Post entity) {
        String SQL = """
                     INSERT INTO Posts(post_name,
                                       image_content_type,
                                       image_payload,
                                       text,
                                       likes_count) VALUES(?, ?, ?, ?, ?)
                     """;

        jdbcTemplate.update(
                SQL,
                entity.getName(),
                entity.getPostImage().getContentType(),
                entity.getPostImage().getPayload(),
                entity.getText(),
                entity.getLikesCount());

        return jdbcTemplate.queryForObject("SELECT * FROM Posts ORDER BY id DESC LIMIT 1", rowMapper);
    }

    @Override
    public Post update(Post entity) {
        String SQL = """
                     UPDATE Posts SET post_name = ?,
                                      image_content_type = ?,
                                      image_payload = ?,
                                      text = ?,
                                      likes_count = ?
                                      WHERE id = ?
                     """;

        jdbcTemplate.update(
                SQL,
                entity.getName(),
                entity.getPostImage().getContentType(),
                entity.getPostImage().getPayload(),
                entity.getText(),
                entity.getLikesCount(),
                entity.getId());

        return jdbcTemplate.queryForObject("SELECT * FROM Posts WHERE id=?", rowMapper, entity.getId());
    }

    @Override
    public Optional<Post> findById(Long id) {
        String SQL = "SELECT * FROM Posts WHERE id = ?";
        return jdbcTemplate.query(SQL, rowMapper, id).stream().findFirst();
    }

    @Override
    public void remove(Long id) {
        String SQL = "DELETE FROM Posts WHERE id = ?";
        jdbcTemplate.update(SQL, id);
    }
}
