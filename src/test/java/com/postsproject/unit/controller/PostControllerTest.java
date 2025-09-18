package com.postsproject.unit.controller;

import com.postsproject.configuration.mock.controller.PostControllerUnitMockConfiguration;
import com.postsproject.controller.PostController;
import com.postsproject.dto.request.RequestPostDTO;
import com.postsproject.dto.response.ResponsePostEditFormDTO;
import com.postsproject.dto.response.ResponsePostPageDTO;
import com.postsproject.dto.response.ResponsePostsTapeDTO;
import com.postsproject.model.Comment;
import com.postsproject.model.Post;
import com.postsproject.model.PostImage;
import com.postsproject.model.Tag;
import com.postsproject.service.blg.interfaces.PostService;
import com.postsproject.service.blg.interfaces.TagService;
import com.postsproject.service.util.validator.RequestPostValidator;
import com.postsproject.util.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringJUnitConfig(PostControllerUnitMockConfiguration.class)
class PostControllerTest {
    @Autowired
    protected PostController postController;

    @Autowired
    protected PostService postService;

    @Autowired
    protected TagService tagService;

    @Autowired
    protected RequestPostValidator validator;

    private MockMvc mockMvc;

    @BeforeEach
    protected void setUp() {
        reset(postService, tagService, validator);
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setViewResolvers((viewName, locale) -> {
                    if (viewName.startsWith("redirect:")) {
                        String target = viewName.substring("redirect:".length());
                        return new RedirectView(target, true);
                    }
                    return new InternalResourceView(viewName);
                })
                .build();
    }

    @Test
    public void testPostsPage_searchTagIsEmpty() throws Exception {
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

        Page<ResponsePostsTapeDTO> tapeDTO = new Page<>(
                "",
                5,
                1,
                1,
                List.of(
                        new ResponsePostsTapeDTO(
                                1L,
                                "Post 1",
                                "data:image/png;base64,[1]",
                                "Post 1 text",
                                Collections.emptyList(),
                                5L,
                                1L),
                        new ResponsePostsTapeDTO(
                                2L,
                                "Post 2",
                                "data:image/png;base64,[2]",
                                "Post 2 text",
                                Collections.emptyList(),
                                10L,
                                2L),
                        new ResponsePostsTapeDTO(
                                3L,
                                "Post 3",
                                "data:image/png;base64,[3]",
                                "Post 3 text",
                                Collections.emptyList(),
                                15L,
                                3L),
                        new ResponsePostsTapeDTO(
                                4L,
                                "Post 4",
                                "data:image/png;base64,[4]",
                                "Post 4 text",
                                Collections.emptyList(),
                                20L,
                                4L),
                        new ResponsePostsTapeDTO(
                                5L,
                                "Post 5",
                                "data:image/png;base64,[5]",
                                "Post 5 text",
                                Collections.emptyList(),
                                25L,
                                5L)
                )
        );

        when(postService.findAllByPage(5L, 1L)).thenReturn(postPage);
        when(postService.mapToResponseTapeDto(postPage)).thenReturn(tapeDTO);

        mockMvc.perform(get("/posts")
                        .param("search", "")
                        .param("pageSize", "5")
                        .param("pageNumber", "1"))
                .andExpect(model().attribute("page", tapeDTO))
                .andExpect(view().name("posts"))
                .andExpect(status().isOk());

        verify(postService, times(1)).findAllByPage(5L, 1L);
        verify(postService, times(1)).mapToResponseTapeDto(postPage);
        verifyNoMoreInteractions(postService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(validator);
    }

    @Test
    public void testPostsPage_searchTagIsNotEmpty() throws Exception {
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

        Page<ResponsePostsTapeDTO> tapeDTO = new Page<>(
                "Tag1",
                5,
                1,
                1,
                List.of(
                        new ResponsePostsTapeDTO(
                                1L,
                                "Post 1",
                                "data:image/png;base64,[1]",
                                "Post 1 text",
                                List.of(new Tag(1L, "Tag1")),
                                5L,
                                1L),
                        new ResponsePostsTapeDTO(
                                2L,
                                "Post 2",
                                "data:image/png;base64,[2]",
                                "Post 2 text",
                                List.of(new Tag(1L, "Tag1")),
                                10L,
                                2L),
                        new ResponsePostsTapeDTO(
                                3L,
                                "Post 3",
                                "data:image/png;base64,[3]",
                                "Post 3 text",
                                List.of(new Tag(1L, "Tag1")),
                                15L,
                                3L),
                        new ResponsePostsTapeDTO(
                                4L,
                                "Post 4",
                                "data:image/png;base64,[4]",
                                "Post 4 text",
                                List.of(new Tag(1L, "Tag1")),
                                20L,
                                4L),
                        new ResponsePostsTapeDTO(
                                5L,
                                "Post 5",
                                "data:image/png;base64,[5]",
                                "Post 5 text",
                                List.of(new Tag(1L, "Tag1")),
                                25L,
                                5L)
                )
        );

        when(postService.findAllByTagAndPage("Tag1", 5L, 1L)).thenReturn(postPage);
        when(postService.mapToResponseTapeDto(postPage)).thenReturn(tapeDTO);

        mockMvc.perform(get("/posts")
                        .param("search", "Tag1")
                        .param("pageSize", "5")
                        .param("pageNumber", "1"))
                .andExpect(model().attribute("page", tapeDTO))
                .andExpect(view().name("posts"))
                .andExpect(status().isOk());

        verify(postService, times(1)).findAllByTagAndPage("Tag1", 5L, 1L);
        verify(postService, times(1)).mapToResponseTapeDto(postPage);
        verifyNoMoreInteractions(postService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(validator);
    }

    @Test
    public void testSavePostFormPage() throws Exception {
        mockMvc.perform(get("/posts/add"))
                .andExpect(view().name("post-form"))
                .andExpect(model().attribute("post", nullValue()))
                .andExpect(status().isOk());
    }

    @Test
    public void testEditPostFormPage() throws Exception {
        Post post = new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                0
        );
        ResponsePostEditFormDTO postEditFormDTO = new ResponsePostEditFormDTO(
                1L,
                "Post 1",
                "data:image/png;base64,[1]",
                "Post 1 text",
                "Tag1 Tag2");

        when(postService.findPostById(1L)).thenReturn(post);
        when(postService.mapToResponseEditDto(post)).thenReturn(postEditFormDTO);

        mockMvc.perform(get("/posts/edit/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("post-form"))
                .andExpect(model().attribute("post", postEditFormDTO));

        verify(postService, times(1)).findPostById(1L);
        verify(postService, times(1)).mapToResponseEditDto(post);
        verifyNoMoreInteractions(postService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(validator);
    }

    @Test
    public void testPostPage() throws Exception {
        Post post = new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                1L
        );

        ResponsePostPageDTO postPageDTO = new ResponsePostPageDTO(
                1L,
                "Post 1",
                "data:image/png;base64,[1]",
                "Post 1 text",
                List.of(new Tag(1L, "Tag1")),
                1L,
                List.of(new Comment(1L, 1L, "Post 1 comment")));

        when(postService.findPostById(1L)).thenReturn(post);
        when(postService.mapToPostPageDto(post)).thenReturn(postPageDTO);

        mockMvc.perform(get("/posts/{id}", 1L))
                        .andExpect(view().name("post"))
                        .andExpect(model().attribute("post", postPageDTO))
                        .andExpect(status().isOk());

        verify(postService, times(1)).findPostById(1L);
        verify(postService, times(1)).mapToPostPageDto(post);
    }

    @Test
    public void testSavePost() throws Exception {
        Post post = new Post(
                null,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                0
        );

        doNothing().when(validator).validate(any(RequestPostDTO.class));
        when(postService.mapToModel(any(RequestPostDTO.class))).thenReturn(post);
        when(postService.save(any(Post.class))).thenAnswer(invocationOnMock -> {
            Post p = invocationOnMock.getArgument(0);
            p.setId(1L);
            return p;
        });

        Post saved = new Post(
                1L,
                "Post 1",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                0
        );
        doNothing().when(tagService).parseAndSaveTagsForPost("Tag1 Tag2", saved);

        mockMvc.perform(multipart("/posts/add")
                        .file(new MockMultipartFile(
                                "image",
                                "image.png",
                                "image/png",
                                new byte[]{1}
                        ))
                        .param("name", "Post 1")
                        .param("tags", "Tag1 Tag2")
                        .param("text", "Post 1 text")
                )
                .andExpect(redirectedUrl("/posts"))
                .andExpect(status().is3xxRedirection());

        verify(validator, times(1)).validate(any(RequestPostDTO.class));
        verify(postService, times(1)).mapToModel(any(RequestPostDTO.class));
        verify(postService, times(1)).save(any(Post.class));
        verify(tagService, times(1)).parseAndSaveTagsForPost(anyString(), any(Post.class));
        verifyNoMoreInteractions(postService);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    public void testEditPost_postImageIsEmpty() throws Exception {
        PostImage postImage = new PostImage("image/png", new byte[]{1});
        Post post = new Post(
                1L,
                "New post 1",
                postImage,
                "New post 1 text",
                0L
        );

        when(postService.mapToModel(any(RequestPostDTO.class))).thenReturn(post);
        when(postService.update(any(Post.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        when(postService.findPostImageByPostId(1L)).thenReturn(postImage);
        doNothing().when(tagService).parseAndUpdateTagsForPost(anyString(), any(Post.class));

        mockMvc.perform(multipart("/posts/edit/{id}", 1L)
                        .file(new MockMultipartFile(
                                "image",
                                "image.png",
                                "image/png",
                                InputStream.nullInputStream()
                        ))
                        .param("name", "New post 1")
                        .param("tags", "Tag1 Tag2")
                        .param("text", "New post 1 text")
                )
                .andExpect(redirectedUrl("/posts/1"))
                .andExpect(status().is3xxRedirection());

        verify(postService, times(1)).mapToModel(any(RequestPostDTO.class));
        verify(postService, times(1)).update(any(Post.class));
        verify(tagService, times(1)).parseAndUpdateTagsForPost(anyString(), any(Post.class));
        verify(postService, times(1)).findPostImageByPostId(1L);
        verifyNoMoreInteractions(postService);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    public void testEditPost_postImageIsNotEmpty() throws Exception {
        Post post = new Post(
                1L,
                "New post 1",
                new PostImage("image/png", new byte[]{1}),
                "New post 1 text",
                0L
        );

        when(postService.mapToModel(any(RequestPostDTO.class))).thenReturn(post);
        when(postService.update(any(Post.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        doNothing().when(tagService).parseAndUpdateTagsForPost(anyString(), any(Post.class));

        mockMvc.perform(multipart("/posts/edit/{id}", 1L)
                        .file(new MockMultipartFile(
                                "image",
                                "image.png",
                                "image/png",
                                new byte[]{2}
                        ))
                        .param("name", "New post 1")
                        .param("tags", "Tag1 Tag2")
                        .param("text", "New post 1 text")
                )
                .andExpect(redirectedUrl("/posts/1"))
                .andExpect(status().is3xxRedirection());

        verify(postService, times(1)).mapToModel(any(RequestPostDTO.class));
        verify(postService, times(1)).update(any(Post.class));
        verify(tagService, times(1)).parseAndUpdateTagsForPost(anyString(), any(Post.class));
        verify(postService, never()).findPostImageByPostId(1L);
        verifyNoMoreInteractions(postService);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(tagService);
    }

    @Test
    public void testAddLike() throws Exception {
        doNothing().when(postService).addPostLike(1L);

        mockMvc.perform(post("/posts/{id}/add-like", 1L))
                .andExpect(redirectedUrl("/posts/1"))
                .andExpect(status().is3xxRedirection());

        verify(postService, times(1)).addPostLike(1L);
        verifyNoMoreInteractions(postService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(validator);
    }

    @Test
    public void testRemove() throws Exception {
        doNothing().when(postService).remove(1L);

        mockMvc.perform(post("/posts/remove/{id}", 1L))
                .andExpect(redirectedUrl("/posts"))
                .andExpect(status().is3xxRedirection());


        verify(postService, times(1)).remove(1L);
        verifyNoMoreInteractions(postService);
        verifyNoInteractions(tagService);
        verifyNoInteractions(validator);
    }
}