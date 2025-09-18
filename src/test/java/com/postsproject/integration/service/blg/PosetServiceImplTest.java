package com.postsproject.integration.service.blg;

import com.postsproject.configuration.ServiceTestConfiguration;
import com.postsproject.dto.request.RequestPostDTO;
import com.postsproject.dto.response.ResponsePostEditFormDTO;
import com.postsproject.dto.response.ResponsePostPageDTO;
import com.postsproject.dto.response.ResponsePostsTapeDTO;
import com.postsproject.integration.BaseRepositoryTestFiller;
import com.postsproject.model.Comment;
import com.postsproject.model.Post;
import com.postsproject.model.PostImage;
import com.postsproject.model.Tag;
import com.postsproject.repository.PostRepositoryImpl;
import com.postsproject.service.blg.PostServiceImpl;
import com.postsproject.service.blg.interfaces.CommentService;
import com.postsproject.util.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ContextHierarchy({
        @ContextConfiguration(name = "service", classes = ServiceTestConfiguration.class)
})
public class PosetServiceImplTest extends BaseRepositoryTestFiller {
    @Autowired
    protected PostServiceImpl postService;

    @Autowired
    protected PostRepositoryImpl postRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostServiceImpl postServiceImpl;

    @BeforeEach
    protected void setUp() {
        setUpPosts();
    }

    @Test
    public void testSave() {
        Post post = new Post(
                null,
                "Post 6",
                new PostImage("image/png", new byte[]{6}),
                "Post 6 text",
                30);

        Post expected = new Post(
                6L,
                "Post 6",
                new PostImage("image/png", new byte[]{6}),
                "Post 6 text",
                30);

        Post result = postService.save(post);

        assertEquals(expected, result);
        assertEquals(Optional.of(expected), postRepository.findById(6L));
    }

    @Test
    public void testUpdate_success() {
        Post post = postService.findPostById(1L);
        post.setName("New Post 1 Name");
        post.setText("New Post 1 text");

        Post expected = new Post(
                1L,
                "New Post 1 Name",
                new PostImage("image/png", new byte[]{1}),
                "New Post 1 text",
                5);

        Post result = postService.update(post);


        assertEquals(expected, result);
        assertEquals(Optional.of(expected), postRepository.findById(1L));
    }

    @Test
    public void testUpdate_throwNoSuchElementException() {
        Post post = new Post(
                9999L,
                "New Post 1 Name",
                new PostImage("image/png", new byte[]{1}),
                "New Post 1 text",
                5);

        assertThrows(NoSuchElementException.class, () -> postService.update(post));
    }


    @Test
    public void testRemove() {
        assertNotNull(postService.findPostById(1L));
        postService.remove(1L);
        assertNull(postService.findPostById(1L));
    }

    @Test
    public void testAddPostLike_success() {
        Post post = postService.findPostById(1L);
        assertEquals(5L, post.getLikesCount());
        postService.addPostLike(1L);
        Post result = postService.findPostById(1L);
        assertEquals(6L, result.getLikesCount());
    }

    @Test
    public void testFindAllByPage() {
        Page<Post> expected = new Page<>("",2, 2, 3, List.of(
                new Post(
                        3L,
                        "Post 3 name",
                        new PostImage("image/png", new byte[]{3}),
                        "Post 3 text",
                        15),
                new Post(
                        4L,
                        "Post 4 name",
                        new PostImage("image/png", new byte[]{4}),
                        "Post 4 text",
                        20)
        ));

        assertEquals(expected, postService.findAllByPage(2, 2));
    }

    @Test
    public void testFindAllByTagAndPage() {
        setUpTags();
        Page<Post> expected = new Page<>("Tag1",2, 2, 2, List.of(
                new Post(
                        4L,
                        "Post 4 name",
                        new PostImage("image/png", new byte[]{4}),
                        "Post 4 text",
                        20),
                new Post(
                        5L,
                        "Post 5 name",
                        new PostImage("image/png", new byte[]{5}),
                        "Post 5 text",
                        25)
        ));

        assertEquals(expected, postService.findAllByTagAndPage("Tag1", 2, 2));
    }

    @Test
    public void testMapToResponseTapeDto() {
        setUpTags();
        setUpComments();

        Page<Post> postPage = new Page<>("",2, 1, 3, List.of(
                new Post(
                        1L,
                        "Post 1 name",
                        new PostImage("image/png", new byte[]{1}),
                        "Post 1 text",
                        5),
                new Post(
                        2L,
                        "Post 2 name",
                        new PostImage("image/png", new byte[]{2}),
                        "Post 2 text",
                        10)
        ));

        Page<ResponsePostsTapeDTO> expected = new Page<>("", 2, 1, 3, List.of(
                new ResponsePostsTapeDTO(
                        1L,
                        "Post 1 name",
                        "data:image/png;base64," + Base64.getEncoder().encodeToString(new byte[]{1}),
                        "Post 1 text",
                        List.of(new Tag(1L, "Tag1"), new Tag(2L, "Tag2")),
                        5L,
                        3L
                ),
                new ResponsePostsTapeDTO(
                        2L,
                        "Post 2 name",
                        "data:image/png;base64," + Base64.getEncoder().encodeToString(new byte[]{2}),
                        "Post 2 text",
                        List.of(new Tag(2L, "Tag2")),
                        10L,
                        2L
                )
        ));

        assertEquals(expected, postService.mapToResponseTapeDto(postPage));
    }

    @Test
    public void testMapToResponseEditDto() {
        setUpTags();

        Post post = new Post(
                1L,
                "Post 1 name",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5);

        ResponsePostEditFormDTO expected = new ResponsePostEditFormDTO(
                1L,
                "Post 1 name",
                "data:image/png;base64," + Base64.getEncoder().encodeToString(new byte[]{1}),
                "Post 1 text",
                "Tag1 Tag2"
        );

        assertEquals(expected, postService.mapToResponseEditDto(post));
    }

    @Test
    public void testMapToPostPageDto() {
        setUpTags();
        setUpComments();

        Post post = new Post(
                1L,
                "Post 1 name",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5);

        ResponsePostPageDTO expected = new ResponsePostPageDTO(
                1L,
                "Post 1 name",
                "data:image/png;base64," + Base64.getEncoder().encodeToString(new byte[]{1}),
                "Post 1 text",
                List.of(new Tag(1L, "Tag1"), new Tag(2L, "Tag2")),
                5L,
                List.of(new Comment(1L, 1L, "Comment 1 text"),
                        new Comment(2L, 1L, "Comment 2 text"),
                        new Comment(3L, 1L, "Comment 3 text"))
        );

        assertEquals(expected, postService.mapToPostPageDto(post));
    }

    @Test
    public void  testMapToModel() throws IOException {
        RequestPostDTO requestPostDTO = new RequestPostDTO(
                "Post 1 name",
                "Tag1 Tag2",
                new MockMultipartFile(
                        "image",
                        "image.png",
                        "image/png",
                        new byte[]{1}),
                "Post 1 text"
        );

        Post expected = new Post(
                null,
                "Post 1 name",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                0L
        );

        assertEquals(expected, postService.mapToModel(requestPostDTO));
    }
}
