package com.postsproject.integration.repository;


import com.postsproject.integration.BaseRepositoryTestFiller;
import com.postsproject.model.Post;
import com.postsproject.model.PostImage;
import com.postsproject.repository.PostRepositoryImpl;
import com.postsproject.util.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostRepositoryImplTest extends BaseRepositoryTestFiller {
    @Autowired
    protected PostRepositoryImpl postRepository;

    @BeforeEach
    protected void setUp() {
        setUpPosts();
    }

    @Nested
    class PaginationTests {
        @BeforeEach
        protected void setUp() {
            setUpTags();
        }

        @ParameterizedTest
        @MethodSource("findAllSource")
        public void testFindAll(long pageSize, long pageNumber, Page<Post> expected) {
            assertEquals(expected, postRepository.findAll(pageSize, pageNumber));
        }

        @ParameterizedTest
        @MethodSource("findAllByTagSource")
        public void testFindAllByTag(String search, long pageSize, long pageNumber, Page<Post> expected) {
            assertEquals(expected, postRepository.findAllByTag(search, pageSize, pageNumber));
        }


        private static Stream<Arguments> findAllSource() {
            return Stream.of(
                    Arguments.of(1, 3,
                            new Page<>("", 1, 3, 5,
                                    List.of(
                                            createPost(3L,
                                                    "Post 3 name",
                                                    "image/png",
                                                    new byte[] {3},
                                                    "Post 3 text",
                                                    15)
                                    ))),
                    Arguments.of(2, 1,
                            new Page<>("", 2, 1, 3,
                                    List.of(
                                            createPost(1L,
                                                    "Post 1 name",
                                                    "image/png",
                                                    new byte[] {1},
                                                    "Post 1 text",
                                                    5),
                                            createPost(2L,
                                                    "Post 2 name",
                                                    "image/png",
                                                    new byte[] {2},
                                                    "Post 2 text",
                                                    10)
                                    ))),
                    Arguments.of(3, 2, new Page<>("", 3, 2, 2,
                            List.of(
                                    createPost(4L,
                                            "Post 4 name",
                                            "image/png",
                                            new byte[] {4},
                                            "Post 4 text",
                                            20),
                                    createPost(5L,
                                            "Post 5 name",
                                            "image/png",
                                            new byte[] {5},
                                            "Post 5 text",
                                            25)
                            ))),
                    Arguments.of(5, 1, new Page<>("", 5, 1, 1,
                            List.of(
                                    createPost(1L,
                                            "Post 1 name",
                                            "image/png",
                                            new byte[] {1},
                                            "Post 1 text",
                                            5),
                                    createPost(2L,
                                            "Post 2 name",
                                            "image/png",
                                            new byte[] {2},
                                            "Post 2 text",
                                            10),
                                    createPost(3L,
                                            "Post 3 name",
                                            "image/png",
                                            new byte[] {3},
                                            "Post 3 text",
                                            15),
                                    createPost(4L,
                                            "Post 4 name",
                                            "image/png",
                                            new byte[] {4},
                                            "Post 4 text",
                                            20),
                                    createPost(5L,
                                            "Post 5 name",
                                            "image/png",
                                            new byte[] {5},
                                            "Post 5 text",
                                            25)
                            )))
            );
        }

        private static Stream<Arguments> findAllByTagSource() {
            return Stream.of(
                    Arguments.of("Tag1", 1, 3,
                            new Page<>("Tag1", 1, 3, 4,
                                    List.of(
                                            createPost(4L,
                                                    "Post 4 name",
                                                    "image/png",
                                                    new byte[] {4},
                                                    "Post 4 text",
                                                    20)
                                    ))),
                    Arguments.of("Tag1", 2, 2,
                            new Page<>("Tag1", 2, 2, 2,
                                    List.of(
                                            createPost(4L,
                                                    "Post 4 name",
                                                    "image/png",
                                                    new byte[] {4},
                                                    "Post 4 text",
                                                    20),
                                            createPost(5L,
                                                    "Post 5 name",
                                                    "image/png",
                                                    new byte[] {5},
                                                    "Post 5 text",
                                                    25)
                                    ))),
                    Arguments.of("Tag1", 5, 1,
                            new Page<>("Tag1", 5, 1, 1,
                                    List.of(
                                            createPost(1L,
                                                    "Post 1 name",
                                                    "image/png",
                                                    new byte[] {1},
                                                    "Post 1 text",
                                                    5),
                                            createPost(3L,
                                                    "Post 3 name",
                                                    "image/png",
                                                    new byte[] {3},
                                                    "Post 3 text",
                                                    15),
                                            createPost(4L,
                                                    "Post 4 name",
                                                    "image/png",
                                                    new byte[] {4},
                                                    "Post 4 text",
                                                    20),
                                            createPost(5L,
                                                    "Post 5 name",
                                                    "image/png",
                                                    new byte[] {5},
                                                    "Post 5 text",
                                                    25)
                                    ))),
                    Arguments.of("Tag2", 2, 1,
                            new Page<>("Tag2", 2, 1, 1,
                                    List.of(
                                            createPost(1L,
                                                    "Post 1 name",
                                                    "image/png",
                                                    new byte[] {1},
                                                    "Post 1 text",
                                                    5),
                                            createPost(2L,
                                                    "Post 2 name",
                                                    "image/png",
                                                    new byte[] {2},
                                                    "Post 2 text",
                                                    10)
                                    ))),
                    Arguments.of("Tag4", 1, 1,
                            new Page<>("Tag4", 1, 1, 1,
                                    List.of(
                                            createPost(4L,
                                                    "Post 4 name",
                                                    "image/png",
                                                    new byte[] {4},
                                                    "Post 4 text",
                                                    20)
                                    )))
            );
        }
    }

    @Test
    public void testSave() {
        Post post = createPost(
                null,
                "Post 6 name",
                "image/png",
                new byte[]{6},
                "Post 6 text",
                30
        );

        Post expected = createPost(
                6L,
                "Post 6 name",
                "image/png",
                new byte[]{6},
                "Post 6 text",
                30
        );

        Post result = postRepository.save(post);

        assertEquals(expected, result);
        assertEquals(expected, findById(expected.getId()));
    }

    @Test
    public void testUpdate() {
        Post updatable = findById(1L);
        assertNotNull(updatable);
        updatable.setName("New Post 1 name");
        updatable.setText("New Post 1 text");

        Post result = postRepository.update(updatable);

        Post expected = createPost(
                1L,
                "New Post 1 name",
                "image/png",
                new byte[]{1},
                "New Post 1 text",
                5
        );

        assertEquals(expected, result);
        assertEquals(expected, findById(expected.getId()));
    }

    @Test
    public void testFindById() {
        Post expected = createPost(3L,
                "Post 3 name",
                "image/png",
                new byte[] {3},
                "Post 3 text",
                15
        );

        assertEquals(Optional.of(expected), postRepository.findById(expected.getId()));
    }

    @Test
    public void testRemove() {
        assertNotNull(findById(2L));
        postRepository.remove(2L);
        assertEquals(null, findById(2L));
    }

    private static Post createPost(Long id,
                              String postName,
                              String postImageContentType,
                              byte[] postImagePayload,
                              String text,
                              long likesCount) {
        return new Post(id, postName, new PostImage(postImageContentType, postImagePayload), text, likesCount);
    }

    protected static final RowMapper<Post> rowMapper = (rs, rowNum) -> {
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

    protected Post findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM Posts WHERE id=?", rowMapper, id)
                .stream()
                .findFirst()
                .orElse(null);
    }
}