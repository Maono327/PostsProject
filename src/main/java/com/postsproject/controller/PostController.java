package com.postsproject.controller;

import com.postsproject.dto.request.RequestPostDTO;
import com.postsproject.dto.response.ResponsePostsTapeDTO;
import com.postsproject.model.Post;
import com.postsproject.service.blg.interfaces.PostService;
import com.postsproject.service.blg.interfaces.TagService;
import com.postsproject.util.Page;
import com.postsproject.service.util.validator.interfaces.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final Validator<RequestPostDTO> requestPostDTOValidator;
    private final PostService postService;
    private final TagService tagService;

    @Autowired
    public PostController(Validator<RequestPostDTO> requestPostDTOValidator,
                          PostService postService,
                          TagService tagService) {
        this.requestPostDTOValidator = requestPostDTOValidator;
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping
    public String postsPage(
            @RequestParam(name = "search", required = false, defaultValue = "") String searchTag,
            @RequestParam(name = "pageSize", required = false, defaultValue = "5") Long pageSize,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Long pageNumber,
            Model model) {

        Page<ResponsePostsTapeDTO> postList =
                postService.mapToResponseTapeDto(searchTag.isEmpty() ?
                        postService.findAllByPage(pageSize, pageNumber) :
                        postService.findAllByTagAndPage(searchTag, pageSize, pageNumber));

        model.addAttribute("page", postList);
        return "posts";
    }

    @GetMapping("/add")
    public String savePostFormPage(Model model) {
        model.addAttribute("post", null);
        return "post-form";
    }

    @PostMapping("/add")
    public String savePost(
            @ModelAttribute RequestPostDTO postDTO
            ) throws IOException {

        requestPostDTOValidator.validate(postDTO);

        Post saved = postService.save(postService.mapToModel(postDTO));
        tagService.parseAndSaveTagsForPost(postDTO.tags(), saved);

        return "redirect:/posts";
    }

    @GetMapping("/edit/{id}")
    public String editPostFormPage(
            @PathVariable("id") Long postId,
            Model model) {
        model.addAttribute("post",
                postService.mapToResponseEditDto(postService.findPostById(postId)));
        return "post-form";
    }

    @PostMapping("/edit/{id}")
    public String editPost(
            @PathVariable("id") Long postId,
            @ModelAttribute RequestPostDTO postDTO
    ) throws IOException {

        Post post = postService.mapToModel(postDTO);
        post.setId(postId);

        if (postDTO.image().isEmpty()) {
            post.setPostImage(postService.findPostImageByPostId(postId));
        }

        Post updated = postService.update(post);
        tagService.parseAndUpdateTagsForPost(postDTO.tags(), updated);

        return "redirect:/posts/" + postId;
    }

    @GetMapping("/{id}")
    public String postPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("post", postService.mapToPostPageDto(postService.findPostById(id)));
        return "post";
    }

    @PostMapping("/{id}/add-like")
    public String addLike(@PathVariable("id") Long id) {
        postService.addPostLike(id);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/remove/{id}")
    public String removePost(@PathVariable("id") Long id) {
        postService.remove(id);
        return "redirect:/posts";
    }
}
