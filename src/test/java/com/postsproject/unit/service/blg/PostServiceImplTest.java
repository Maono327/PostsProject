package com.postsproject.unit.service.blg;

import com.postsproject.dto.request.RequestPostDTO;
import com.postsproject.dto.response.ResponsePostEditFormDTO;
import com.postsproject.dto.response.ResponsePostPageDTO;
import com.postsproject.dto.response.ResponsePostsTapeDTO;
import com.postsproject.model.Comment;
import com.postsproject.model.Post;
import com.postsproject.model.PostImage;
import com.postsproject.model.Tag;
import com.postsproject.repository.PostRepositoryImpl;
import com.postsproject.repository.interfaces.PostRepository;
import com.postsproject.service.blg.CommentServiceImpl;
import com.postsproject.service.blg.PostServiceImpl;
import com.postsproject.service.blg.TagServiceImpl;
import com.postsproject.service.blg.interfaces.CommentService;
import com.postsproject.service.blg.interfaces.TagService;
import com.postsproject.service.util.B64Transformer;
import com.postsproject.util.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
        PostServiceImpl.class,
        PostRepositoryImpl.class,
        CommentServiceImpl.class,
        TagServiceImpl.class,
        B64Transformer.class
})
class PostServiceImplTest {
    @Autowired
    protected PostServiceImpl postService;

    @MockitoBean(reset = MockReset.BEFORE)
    protected CommentService commentService;

    @MockitoBean(reset = MockReset.BEFORE)
    protected TagService tagService;

    @MockitoBean(reset = MockReset.BEFORE)
    protected B64Transformer b64Transformer;

    @MockitoBean(reset = MockReset.BEFORE)
    protected PostRepository postRepository;

    @Test
    public void testSave() {
        Post post = new Post(
                null,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5);


        Post expected = new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5);

        when(postRepository.save(post)).thenAnswer(invocationOnMock -> {
            Post p = invocationOnMock.getArgument(0);
            p.setId(1L);
            return p;
        });

        Post result = postService.save(post);

        assertEquals(expected, result);
        verify(postRepository, times(1)).save(post);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(b64Transformer);
    }

    @Test
    public void testUpdate_success () {
        Post postFromDB = new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5);

        Post updatable = new Post(
                1L,
                "New Post 1",
                new PostImage("image/png", new byte[]{1,2}),
                "New Post 1 text",
                5);

        when(postRepository.findById(1L)).thenReturn(Optional.of(postFromDB));

        Post expected = new Post(
                1L,
                "New Post 1",
                new PostImage("image/png", new byte[]{1,2}),
                "New Post 1 text",
                5);

        when(postRepository.update(postFromDB)).thenReturn(postFromDB);

        Post result = postService.update(updatable);

        assertEquals(expected, result);
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).update(updatable);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(b64Transformer);
    }

    @Test
    public void testUpdate_throwNoSuchElementException() {
        Post postToUpdate = new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5);

        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> postService.update(postToUpdate));
        verify(postRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(b64Transformer);
    }

    @Test
    public void testRemove() {
        doNothing().when(postRepository).remove(1L);

        postService.remove(1L);

        verify(postRepository, times(1)).remove(1L);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(b64Transformer);
    }

    @Test
    public void testAddPostLike_success() {
        Post postFromDB = new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5);

        when(postRepository.findById(1L)).thenReturn(Optional.of(postFromDB));

        Post updatable = new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                6);

        postService.addPostLike(1L);
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).update(updatable);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(b64Transformer);
    }

    @Test
    public void testAddPostLike_throwNoSuchElementException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> postService.addPostLike(1L));
        verify(postRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(b64Transformer);
    }

    @Test
    public void testFindAllByPage() {
        Page<Post> postPage = new Page<>("", 5, 1, 1,
                List.of(
                        new Post(
                                1L,
                                "Post 1",
                                new PostImage("image/png", new byte[]{1}),
                                "Post 1 text",
                                5),
                        new Post(
                                2L,
                                "Post 2",
                                new PostImage("image/png", new byte[]{2}),
                                "Post 2 text",
                                10),
                        new Post(
                                3L,
                                "Post 3",
                                new PostImage("image/png", new byte[]{3}),
                                "Post 3 text",
                                15),
                        new Post(
                                4L,
                                "Post 4",
                                new PostImage("image/png", new byte[]{4}),
                                "Post 4 text",
                                20),
                        new Post(
                                5L,
                                "Post 5",
                                new PostImage("image/png", new byte[]{5}),
                                "Post 5 text",
                                25)
                )
        );

        when(postRepository.findAll(any(Long.class), any(Long.class))).thenReturn(postPage);
        Page<Post> result = postService.findAllByPage(5L, 1L);
        assertEquals(postPage, result);
        verify(postRepository, times(1)).findAll(5L, 1L);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(b64Transformer);
    }

    @Test
    public void testFindAllByTagAndPage() {
        Page<Post> postPage = new Page<>("Tag1", 5, 1, 1,
                List.of(
                        new Post(
                                1L,
                                "Post 1",
                                new PostImage("image/png", new byte[]{1}),
                                "Post 1 text",
                                5),
                        new Post(
                                2L,
                                "Post 2",
                                new PostImage("image/png", new byte[]{2}),
                                "Post 2 text",
                                10),
                        new Post(
                                3L,
                                "Post 3",
                                new PostImage("image/png", new byte[]{3}),
                                "Post 3 text",
                                15)
                )
        );

        when(postRepository.findAllByTag(any(String.class), any(Long.class), any(Long.class))).thenReturn(postPage);
        Page<Post> result = postService.findAllByTagAndPage("Tag1", 5, 1);
        assertEquals(postPage, result);
        verify(postRepository, times(1)).findAllByTag("Tag1", 5, 1);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(b64Transformer);
    }

    @Test
    public void testFindById_success() {
        Post post = new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post expected = new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5);

        Post result = postService.findPostById(1L);
        assertEquals(expected, result);
        verify(postRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(b64Transformer);
    }

    @Test
    public void testFindPostImageByPostId_success() {
        Post post = new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        PostImage expected = new PostImage("image/png", new byte[]{1});

        PostImage result = postService.findPostImageByPostId(1L);
        assertEquals(expected, result);
        verify(postRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(b64Transformer);
    }

    @Test
    public void testFindPostImageByPostId_throwNoSuchElementException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> postService.findPostImageByPostId(1L));
        verify(postRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(commentService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(b64Transformer);
    }

    @Test
    public void testMapToResponseTapeDto() {
        Page<Post> postPage = new Page<>("", 5, 1, 1,
                List.of(
                        new Post(
                                1L,
                                "Post 1",
                                new PostImage("image/png", new byte[]{1}),
                                "Post 1 text",
                                5),
                        new Post(
                                2L,
                                "Post 2",
                                new PostImage("image/png", new byte[]{2}),
                                "Post 2 text text text text text text text text text text text text text " +
                                        "text text text text text text text text text text text text",
                                10),
                        new Post(
                                3L,
                                "Post 3",
                                new PostImage("image/png", new byte[]{3}),
                                "Post 3 text",
                                15),
                        new Post(
                                4L,
                                "Post 4",
                                new PostImage("image/png", new byte[]{4}),
                                "Post 4 text",
                                20),
                        new Post(
                                5L,
                                "Post 5",
                                new PostImage("image/png", new byte[]{5}),
                                "Post 5 text",
                                25)
                )
        );

        when(b64Transformer.transform(any(PostImage.class))).thenAnswer(invocationOnMock -> {
            PostImage postImage = invocationOnMock.getArgument(0);
            return "data:" + postImage.getContentType() + ";base64," + Arrays.toString(postImage.getPayload());
        });
        when(tagService.findTagsByPostId(any(Long.class))).thenReturn(Collections.emptyList());
        when(tagService.findTagsByPostId(3L)).thenReturn(List.of(
                new Tag(1L, "Tag1"),
                new Tag(2L, "Tag2"))
        );
        when(tagService.findTagsByPostId(5L)).thenReturn(List.of(
                new Tag(3L, "Tag3")
        ));
        when(commentService.findCommetsByPostId(any(Long.class))).thenReturn(Collections.emptyList());
        when(commentService.findCommetsByPostId(3L)).thenReturn(List.of(
                new Comment(1L, 3L, "Comment 1 Post 3"),
                new Comment(2L, 3L, "Comment 2 Post 3"),
                new Comment(3L, 3L, "Comment 3 Post 3")
        ));
        when(commentService.findCommetsByPostId(4L)).thenReturn(List.of(
                new Comment(4L, 4L, "Comment 1 Post 4"),
                new Comment(5L, 4L, "Comment 2 Post 4")
        ));

        Page<ResponsePostsTapeDTO> expected = new Page<>("", 5, 1, 1,
                List.of(
                        new ResponsePostsTapeDTO(
                                1L,
                                "Post 1",
                                "data:image/png;base64,[1]",
                                "Post 1 text",
                                Collections.emptyList(),
                                5L,
                                0L),
                        new ResponsePostsTapeDTO(
                                2L,
                                "Post 2",
                                "data:image/png;base64,[2]",
                                "Post 2 text text text text text text text text text text text " +
                                        "text text text text text text text text text text text text text t...",
                                Collections.emptyList(),
                                10L,
                                0L),
                        new ResponsePostsTapeDTO(
                                3L,
                                "Post 3",
                                "data:image/png;base64,[3]",
                                "Post 3 text",
                                List.of(
                                        new Tag(1L, "Tag1"),
                                        new Tag(2L, "Tag2")
                                ),
                                15L,
                                3L),
                        new ResponsePostsTapeDTO(
                                4L,
                                "Post 4",
                                "data:image/png;base64,[4]",
                                "Post 4 text",
                                Collections.emptyList(),
                                20L,
                                2L),
                        new ResponsePostsTapeDTO(
                                5L,
                                "Post 5",
                                "data:image/png;base64,[5]",
                                "Post 5 text",
                                List.of(new Tag(3L, "Tag3")),
                                25L,
                                0L)
                )
        );

        Page<ResponsePostsTapeDTO> result = postService.mapToResponseTapeDto(postPage);
        assertEquals(expected, result);
        verify(b64Transformer, times(5)).transform(any(PostImage.class));
        verify(tagService, times(5)).findTagsByPostId(any(Long.class));
        verify(commentService, times(5)).findCommetsByPostId(any(Long.class));
        verifyNoMoreInteractions(b64Transformer);
        verifyNoMoreInteractions(tagService);
        verifyNoMoreInteractions(commentService);
        verifyNoInteractions(postRepository);
    }

    @Test
    public void testMapToResponseEditDto() {
        Post post =  new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5
        );

        when(tagService.findTagsByPostId(1L)).thenReturn(List.of(
                new Tag(1L, "Tag1"),
                new Tag(2L, "Tag2"),
                new Tag(3L, "Tag3"))
        );

        when(b64Transformer.transform(post.getPostImage())).thenReturn("data:image/png;base64,[1]");

        ResponsePostEditFormDTO expected = new ResponsePostEditFormDTO(
                1L,
                "Post 1",
                "data:image/png;base64,[1]",
                "Post 1 text",
                "Tag1 Tag2 Tag3"
        );

        ResponsePostEditFormDTO result = postService.mapToResponseEditDto(post);
        assertEquals(expected, result);
        verify(tagService, times(1)).findTagsByPostId(1L);
        verify(b64Transformer, times(1)).transform(post.getPostImage());
        verifyNoMoreInteractions(b64Transformer);
        verifyNoMoreInteractions(tagService);
        verifyNoInteractions(commentService);
        verifyNoInteractions(postRepository);
    }

    @Test
    public void testMapToPostPageDto() {
        Post post =  new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5L
        );

        when(b64Transformer.transform(any(PostImage.class))).thenReturn("data:image/png;base64,[1]");
        when(tagService.findTagsByPostId(any(Long.class))).thenReturn(List.of(new Tag(1L, "Tag1"),
                new Tag(1L, "Tag2")));
        when(commentService.findCommetsByPostId(any(Long.class))).thenReturn(List.of(new Comment(1L, 1L, "Comment 1"),
                new Comment(2L, 1L, "Comment 2")));

        ResponsePostPageDTO expected = new ResponsePostPageDTO(
                1L,
                "Post 1",
                "data:image/png;base64,[1]",
                "Post 1 text",
                List.of(new Tag(1L, "Tag1"),
                        new Tag(1L, "Tag2")),
                5L,
                List.of(new Comment(1L, 1L, "Comment 1"),
                        new Comment(2L, 1L, "Comment 2"))
        );

        ResponsePostPageDTO result = postService.mapToPostPageDto(post);

        assertEquals(expected, result);
        verify(b64Transformer, times(1)).transform(post.getPostImage());
        verify(tagService, times(1)).findTagsByPostId(1L);
        verify(commentService, times(1)).findCommetsByPostId(1L);
        verifyNoMoreInteractions(b64Transformer);
        verifyNoMoreInteractions(tagService);
        verifyNoMoreInteractions(commentService);
        verifyNoInteractions(postRepository);
    }

    @Test
    public void testMapToModel() throws IOException {
        RequestPostDTO dto = new RequestPostDTO(
                "Post 1",
                "Tag1 Tag2 Tag3",
                new MockMultipartFile(
                        "image",
                        "image.png",
                        "image/png",
                        new byte[]{1}
                ),
                "Post 1 text"
        );

        Post expected =  new Post(
                null,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                0L
        );

        Post result = postService.mapToModel(dto);
        assertEquals(expected, result);
        verifyNoInteractions(b64Transformer);
        verifyNoInteractions(tagService);
        verifyNoInteractions(commentService);
        verifyNoInteractions(postRepository);
    }
}