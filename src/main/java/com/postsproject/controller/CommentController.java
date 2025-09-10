package com.postsproject.controller;

import com.postsproject.model.Comment;
import com.postsproject.service.blg.interfaces.CommentService;
import com.postsproject.service.util.validator.interfaces.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/posts/{id}")
public class CommentController {

    private final CommentService commentService;
    private final Validator<String> commentTextValidator;

    @Autowired
    public CommentController(CommentService commentService,
                             Validator<String> commentTextValidator) {
        this.commentService = commentService;
        this.commentTextValidator = commentTextValidator;
    }

    @PostMapping("/add-comment")
    public String addCommentToPost(
            @PathVariable("id") Long postId,
            @RequestParam("text") String text
    ) {
        commentTextValidator.validate(text);
        commentService.save(new Comment(null, postId, text));
        return "redirect:/posts/" + postId;
    }

    @PostMapping(path = "/edit-comment/{commentId}")
    public String editComment(
            @PathVariable("id") Long postId,
            @PathVariable("commentId") Long commentId,
            @RequestParam("text") String text
    ) {
        commentTextValidator.validate(text);
        Comment commentFromDb = commentService.findCommentById(commentId);
        commentFromDb.setText(text);
        commentService.update(commentFromDb);
        return "redirect:/posts/" + postId;
    }

    @PostMapping(path = "/remove/{commentId}")
    public String removeComment(
            @PathVariable("id") Long postId,
            @PathVariable("commentId") Long commentId
    ) {
        commentService.remove(commentId);
        return "redirect:/posts/" + postId;
    }
}
