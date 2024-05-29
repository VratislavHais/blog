package com.vhais.blog.controller;

import com.vhais.blog.model.Comment;
import com.vhais.blog.model.Post;
import com.vhais.blog.service.CategoryService;
import com.vhais.blog.service.PostService;
import com.vhais.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CategoryService categoryService;
    private final TagService tagService;

    @PostMapping("/create")
    public String createPost(@ModelAttribute Post post, Model model) {
        try {
            postService.savePost(post);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/post/create";
        }
    }

    @GetMapping("/create")
    public String displayPostCreation(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "createPost";
    }

    @GetMapping("{id}")
    public String displayPost(@PathVariable String id, Model model) {
        try {
            Post post = postService.getPostById(id);
            model.addAttribute("post", post);
            model.addAttribute("newComment", new Comment());
            return "viewPost";
        } catch (NumberFormatException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }
}
