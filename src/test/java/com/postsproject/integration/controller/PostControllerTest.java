package com.postsproject.integration.controller;

import com.postsproject.configuration.ServiceTestConfiguration;
import com.postsproject.configuration.WebApplicationTestConfiguration;
import com.postsproject.dto.response.ResponsePostEditFormDTO;
import com.postsproject.dto.response.ResponsePostPageDTO;
import com.postsproject.dto.response.ResponsePostsTapeDTO;
import com.postsproject.integration.BaseRepositoryTestFiller;
import com.postsproject.model.Comment;
import com.postsproject.model.Post;
import com.postsproject.model.PostImage;
import com.postsproject.model.Tag;
import com.postsproject.repository.interfaces.PostRepository;
import com.postsproject.service.blg.interfaces.PostService;
import com.postsproject.service.blg.interfaces.TagService;
import com.postsproject.util.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextHierarchy({
        @ContextConfiguration(name = "service", classes = ServiceTestConfiguration.class),
        @ContextConfiguration(name = "web", classes = WebApplicationTestConfiguration.class)
})
public class PostControllerTest extends BaseRepositoryTestFiller {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected PostService postService;

    @Autowired
    protected TagService tagService;

    @Autowired
    protected PostRepository postRepository;

    protected MockMvc mockMvc;

    @BeforeEach
    protected void setUp() {
        setUpPosts();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testPostsPage_emptyTag() throws Exception {
        setUpTags();
        setUpComments();
        Page<ResponsePostsTapeDTO> postList = new Page<>("", 2, 2, 3, List.of(
                new ResponsePostsTapeDTO(
                        3L,
                        "Post 3 name",
                        "data:image/png;base64," + Base64.getEncoder().encodeToString(new byte[]{3}),
                        "Post 3 text",
                        List.of(new Tag(1L, "Tag1"), new Tag(3L, "Tag3")),
                        15L,
                        1L),
                new ResponsePostsTapeDTO(
                        4L,
                        "Post 4 name",
                        "data:image/png;base64," + Base64.getEncoder().encodeToString(new byte[]{4}),
                        "Post 4 text",
                        List.of(new Tag(1L, "Tag1"), new Tag(4L, "Tag4")),
                        20L,
                        0L)
        ));

        mockMvc.perform(get("/posts")
                .param("search", "")
                .param("pageSize", "2")
                .param("pageNumber", "2"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(view().name("posts"))
            .andExpect(model().attribute("page", postList))
                .andExpect(xpath("//button[@id='backButton']/@value").string("1"))
                .andExpect(xpath("//span[text()='2']").exists())
                .andExpect(xpath("//button[@id='nextButton']/@value").string("3"))
                .andExpect(xpath("count(//div[@class='post'])").number(2.0))
                .andExpect(xpath("count(//select[@id='posts-per-page-selection']/option)")
                        .number(5.0))

                .andExpect(xpath("//div[@id='posts-tape'][1]//h2/a").string("Post 3 name"))
                .andExpect(xpath("(//div[@class='post'])[1]//span[contains(., '#Tag1')]").exists())
                .andExpect(xpath("(//div[@class='post'])[1]//span[contains(., '#Tag3')]").exists())
                .andExpect(xpath("(//div[@class='post'])[1]//img/@src")
                        .string("data:image/png;base64," +
                                Base64.getEncoder().encodeToString(new byte[]{3})))
                .andExpect(xpath("(//div[@class='post'])[1]//p[contains(., 'Post 3 text')]").exists())
                .andExpect(xpath("(//div[@class='post'])[1]//span[contains(., '15')]").exists())
                .andExpect(xpath("(//div[@class='post'])[1]//span[contains(., '1')]").exists())

                .andExpect(xpath("//div[@id='posts-tape'][2]//h2/a").string("Post 4 name"))
                .andExpect(xpath("(//div[@class='post'])[2]//span[contains(., '#Tag1')]").exists())
                .andExpect(xpath("(//div[@class='post'])[2]//span[contains(., '#Tag4')]").exists())
                .andExpect(xpath("(//div[@class='post'])[2]//img/@src")
                        .string("data:image/png;base64," +
                                Base64.getEncoder().encodeToString(new byte[]{4})))
                .andExpect(xpath("(//div[@class='post'])[2]//p[contains(., 'Post 4 text')]").exists())
                .andExpect(xpath("(//div[@class='post'])[2]//span[contains(., '20')]").exists())
                .andExpect(xpath("(//div[@class='post'])[2]//span[contains(., '0')]").exists());
    }

    @Test
    public void testPostsPage_notEmptyTag() throws Exception {
        setUpTags();
        setUpComments();
        Page<ResponsePostsTapeDTO> postList = new Page<>("Tag1", 5, 1, 1, List.of(
                new ResponsePostsTapeDTO(
                        1L,
                        "Post 1 name",
                        "data:image/png;base64," + Base64.getEncoder().encodeToString(new byte[]{1}),
                        "Post 1 text",
                        List.of(new Tag(1L, "Tag1"), new Tag(2L, "Tag2")),
                        5L,
                        3L),
                new ResponsePostsTapeDTO(
                        3L,
                        "Post 3 name",
                        "data:image/png;base64," + Base64.getEncoder().encodeToString(new byte[]{3}),
                        "Post 3 text",
                        List.of(new Tag(1L, "Tag1"), new Tag(3L, "Tag3")),
                        15L,
                        1L),
                new ResponsePostsTapeDTO(
                        4L,
                        "Post 4 name",
                        "data:image/png;base64," + Base64.getEncoder().encodeToString(new byte[]{4}),
                        "Post 4 text",
                        List.of(new Tag(1L, "Tag1"), new Tag(4L, "Tag4")),
                        20L,
                        0L),
                new ResponsePostsTapeDTO(
                        5L,
                        "Post 5 name",
                        "data:image/png;base64," + Base64.getEncoder().encodeToString(new byte[]{5}),
                        "Post 5 text",
                        List.of(new Tag(1L, "Tag1")),
                        25L,
                        0L)
        ));

        mockMvc.perform(get("/posts")
                        .param("search", "Tag1")
                        .param("pageSize", "5")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts"))
                .andExpect(model().attribute("page", postList))
                .andExpect(xpath("//button[@id='backButton']").doesNotExist())
                .andExpect(xpath("//span[text()='1']").exists())
                .andExpect(xpath("//button[@id='nextButton']").doesNotExist())
                .andExpect(xpath("count(//div[@class='post'])").number(4.0))
                .andExpect(xpath("count(//select[@id='posts-per-page-selection']/option)")
                        .number(5.0))

                .andExpect(xpath("//div[@id='posts-tape'][1]//h2/a").string("Post 1 name"))
                .andExpect(xpath("(//div[@class='post'])[1]//span[contains(., '#Tag1')]").exists())
                .andExpect(xpath("(//div[@class='post'])[1]//span[contains(., '#Tag2')]").exists())
                .andExpect(xpath("(//div[@class='post'])[1]//img/@src")
                        .string("data:image/png;base64," +
                                Base64.getEncoder().encodeToString(new byte[]{1})))
                .andExpect(xpath("(//div[@class='post'])[1]//p[contains(., 'Post 1 text')]").exists())
                .andExpect(xpath("(//div[@class='post'])[1]//span[contains(., '5')]").exists())
                .andExpect(xpath("(//div[@class='post'])[1]//span[contains(., '3')]").exists())

                .andExpect(xpath("//div[@id='posts-tape'][2]//h2/a").string("Post 3 name"))
                .andExpect(xpath("(//div[@class='post'])[2]//span[contains(., '#Tag1')]").exists())
                .andExpect(xpath("(//div[@class='post'])[2]//span[contains(., '#Tag3')]").exists())
                .andExpect(xpath("(//div[@class='post'])[2]//img/@src")
                        .string("data:image/png;base64," +
                                Base64.getEncoder().encodeToString(new byte[]{3})))
                .andExpect(xpath("(//div[@class='post'])[2]//p[contains(., 'Post 3 text')]").exists())
                .andExpect(xpath("(//div[@class='post'])[2]//span[contains(., '15')]").exists())
                .andExpect(xpath("(//div[@class='post'])[2]//span[contains(., '1')]").exists())

                .andExpect(xpath("//div[@id='posts-tape'][3]//h2/a").string("Post 4 name"))
                .andExpect(xpath("(//div[@class='post'])[3]//span[contains(., '#Tag1')]").exists())
                .andExpect(xpath("(//div[@class='post'])[3]//span[contains(., '#Tag4')]").exists())
                .andExpect(xpath("(//div[@class='post'])[3]//img/@src")
                        .string("data:image/png;base64," +
                                Base64.getEncoder().encodeToString(new byte[]{4})))
                .andExpect(xpath("(//div[@class='post'])[3]//p[contains(., 'Post 4 text')]").exists())
                .andExpect(xpath("(//div[@class='post'])[3]//span[contains(., '20')]").exists())
                .andExpect(xpath("(//div[@class='post'])[3]//span[contains(., '0')]").exists())

                .andExpect(xpath("//div[@id='posts-tape'][4]//h2/a").string("Post 5 name"))
                .andExpect(xpath("(//div[@class='post'])[4]//span[contains(., '#Tag1')]").exists())
                .andExpect(xpath("(//div[@class='post'])[4]//img/@src")
                        .string("data:image/png;base64," +
                                Base64.getEncoder().encodeToString(new byte[]{5})))
                .andExpect(xpath("(//div[@class='post'])[4]//p[contains(., 'Post 5 text')]").exists())
                .andExpect(xpath("(//div[@class='post'])[4]//span[contains(., '25')]").exists())
                .andExpect(xpath("(//div[@class='post'])[4]//span[contains(., '0')]").exists());
    }

    @Test
    public void testSaveFormPostPage() throws Exception {
        mockMvc.perform(get("/posts/add"))
                .andExpect(model().attribute("post", nullValue()))
                .andExpect(view().name("post-form"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSavePost() throws Exception {
        assertTrue(postRepository.findById(6L).isEmpty());
        mockMvc.perform(multipart("/posts/add")
                        .file(new MockMultipartFile(
                                "image",
                                "image.png",
                                "image/png",
                                new byte[]{6}
                        ))
                        .param("name", "Post 6 name")
                        .param("tags", "Tag1 Tag2 Tag6")
                        .param( "text","Post 6 text"))
                .andExpect(redirectedUrl("/posts"))
                .andExpect(status().is3xxRedirection());
        assertTrue(postRepository.findById(6L).isPresent());
    }

    @Test
    public void testEditPostForm() throws Exception {
        setUpTags();
        ResponsePostEditFormDTO dto = new ResponsePostEditFormDTO(
                1L,
                "Post 1 name",
                "data:image/png;base64," + Base64.getEncoder().encodeToString(new byte[]{1}),
                "Post 1 text",
                "Tag1 Tag2"
        );
        mockMvc.perform(get("/posts/edit/{id}", 1L))
                .andExpect(model().attribute("post", dto))
                .andExpect(view().name("post-form"))
                .andExpect(status().isOk())
                .andExpect(xpath("//title").string("Редактирование поста"))
                .andExpect(xpath("//div[@class='post']//input[@id='post-name']/@value").string("Post 1 name"))
                .andExpect(xpath("//div[@class='post']//input[@id='post-tags']/@value").string("Tag1 Tag2"))
                .andExpect(xpath("//div[@class='post']//img/@src").exists())
                .andExpect(xpath("//div[@class='post']//textarea[contains(., 'Post 1 text')]").exists());
    }

    @ParameterizedTest
    @MethodSource("imageArguments")
    public void testEditPost(MockMultipartFile multipartFile, PostImage postImage) throws Exception {
        setUpTags();

        Post expectedPostBefore = new Post(
                1L,
                "Post 1 name",
                new PostImage("image/png", new byte[]{1}),
                "Post 1 text",
                5L
        );

        List<Tag> expectedTagsBefore = List.of(new Tag(1L, "Tag1"), new Tag(2L, "Tag2"));

        assertEquals(expectedPostBefore, postService.findPostById(1L));
        assertEquals(expectedTagsBefore, tagService.findTagsByPostId(1L));

        mockMvc.perform(multipart("/posts/edit/{id}", 1L)
                .file(multipartFile)
                .param("name", "New post 1 name")
                .param("text", "New post 1 text")
                .param("tags", "Tag2 Tag3"))
                .andExpect(redirectedUrl("/posts/1"))
                .andExpect(status().is3xxRedirection());

        Post editedPostExpected = new Post(
                1L,
                "New post 1 name",
                postImage,
                "New post 1 text",
                5L
        );

        List<Tag> editedTagsExpected = List.of(new Tag(2L, "Tag2"), new Tag(3L, "Tag3"));

        assertEquals(editedPostExpected, postService.findPostById(1L));
        assertEquals(editedTagsExpected, tagService.findTagsByPostId(1L));
    }

    protected static Stream<Arguments> imageArguments() throws IOException {
        return Stream.of(
                Arguments.of(new MockMultipartFile(
                        "image",
                        "image.png",
                        "image/png",
                        InputStream.nullInputStream()),
                        new PostImage("image/png", new byte[]{1})),
                Arguments.of(new MockMultipartFile(
                                "image",
                                "image.png",
                                "image/png",
                                new byte[]{2}),
                        new PostImage("image/png", new byte[]{2}))
        );
    }

    @Test
    public void testPostPage() throws Exception {
        setUpTags();
        setUpComments();

        ResponsePostPageDTO postPageDTO = new ResponsePostPageDTO(
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

        mockMvc.perform(get("/posts/{id}", 1L))
                .andExpect(model().attribute("post", postPageDTO))
                .andExpect(status().isOk())
                .andExpect(xpath("//div[@id='post']/h1").string("Post 1 name"))
                .andExpect(xpath("//div[@id='post']//span[contains(., '#Tag1')]").exists())
                .andExpect(xpath("//div[@id='post']//span[contains(., '#Tag2')]").exists())
                .andExpect(xpath("//img/@src").exists())
                .andExpect(xpath("//div[@id='post']//p[contains(., 'Post 1 text')]").exists())
                .andExpect(xpath("//div[@id='post']//span[contains(., '5')]").exists())

                .andExpect(xpath("//div[@id='comments']//p[contains(., 'Comment 1 text')]").exists())
                .andExpect(xpath("//div[@id='comments']//p[contains(., 'Comment 2 text')]").exists())
                .andExpect(xpath("//div[@id='comments']//p[contains(., 'Comment 3 text')]").exists());
    }

    @Test
    public void testAddLike() throws Exception {
        assertEquals(5, postService.findPostById(1L).getLikesCount());
        mockMvc.perform(post("/posts/{id}/add-like", 1L))
                .andExpect(redirectedUrl("/posts/1"))
                .andExpect(status().is3xxRedirection());
        assertEquals(6, postService.findPostById(1L).getLikesCount());
    }

    @Test
    public void testRemovePost() throws Exception {
        assertTrue(postRepository.findById(1L).isPresent());
        mockMvc.perform(post("/posts/remove/{id}", 1L))
                .andExpect(redirectedUrl("/posts"))
                .andExpect(status().is3xxRedirection());
        assertTrue(postRepository.findById(1L).isEmpty());
    }
}
