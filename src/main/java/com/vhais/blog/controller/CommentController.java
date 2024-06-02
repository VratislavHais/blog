package com.vhais.blog.controller;

import com.vhais.blog.model.Comment;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.User;
import com.vhais.blog.service.CommentService;
import com.vhais.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    @PostMapping("/{postId}/comment")
    public String postComment(@PathVariable("postId") Long postId,
                              @ModelAttribute Comment comment,
                              Model model) {
        comment.setCreatedAt(LocalDateTime.now());
        Post post = postService.getPostById(postId);
        comment.setPost(post);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        comment.setAuthor(user);
        commentService.saveComment(comment, post, user);
        return "redirect:/post/" + postId;
    }
}
