package com.vhais.blog.controller;

import com.vhais.blog.dto.CommentDTO;
import com.vhais.blog.service.CommentService;
import com.vhais.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    @PostMapping("/{postId}/comment")
    public String postComment(@PathVariable("postId") Long postId,
                              @ModelAttribute CommentDTO commentDTO) {
        commentService.saveCommentUnderPost(commentDTO, postId);
        return "redirect:/post/" + postId;
    }

    @GetMapping("/{postId}/comment/{commentId}/delete")
    public String deleteComment(@PathVariable("postId") Long postId,
                                @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId, postId);
        return "redirect:/post/" + postId;
    }
}
