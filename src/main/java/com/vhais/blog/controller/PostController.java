package com.vhais.blog.controller;

import com.vhais.blog.dto.PostDTO;
import com.vhais.blog.model.Comment;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.Tag;
import com.vhais.blog.model.User;
import com.vhais.blog.service.CategoryService;
import com.vhais.blog.service.PostService;
import com.vhais.blog.service.TagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private static final String HOME = "/home";

    private final PostService postService;
    private final CategoryService categoryService;
    private final TagService tagService;

    @PostMapping("/create")
    public String createPost(@ModelAttribute PostDTO post, Model model) {
        try {
            Post newPost = new Post();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            newPost.setAuthor((User) auth.getPrincipal());
            Set<Tag> tags = tagService.saveAllTags(parseTagField(post.getTags()));
            newPost.setTags(tags);
            newPost.setCategory(categoryService.getCategoryByName(post.getCategory()).orElseThrow(() -> new EntityNotFoundException("Category " + post.getCategory() + " not found")));
            newPost.setContent(post.getContent());
            newPost.setCreatedAt(LocalDateTime.now());
            newPost.setUpdatedAt(LocalDateTime.now());
            newPost.setTitle(post.getTitle());
            postService.savePost(newPost);
            return "redirect:" + HOME;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("post", post);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "createPost";
        }
    }

    private Set<String> parseTagField(String tags) {
        if (tags != null) {
            return Arrays.stream(tags.split(" ")).collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    @GetMapping("/create")
    public String displayPostCreation(Model model) {
        model.addAttribute("post", new PostDTO());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "createPost";
    }

    @GetMapping("{id}")
    public String displayPost(@PathVariable Long id, Model model) {
        try {
            Post post = postService.getPostById(id);
            model.addAttribute("post", post);
            model.addAttribute("newComment", new Comment());
            return "viewPost";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:" + HOME;
        }
    }
}
