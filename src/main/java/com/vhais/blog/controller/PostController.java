package com.vhais.blog.controller;

import com.vhais.blog.dto.CommentDTO;
import com.vhais.blog.dto.PostDTO;
import com.vhais.blog.dto.ResponsePostDTO;
import com.vhais.blog.model.Post;
import com.vhais.blog.repository.CategoryRepository;
import com.vhais.blog.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private static final String HOME = "/home";

    private final PostService postService;
    private final CategoryRepository categoryRepository;

    @PostMapping("/create")
    public String createPost(@ModelAttribute PostDTO post, Model model) {
        try {
            postService.savePost(post);
            return "redirect:" + HOME;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("post", post);
            model.addAttribute("categories", categoryRepository.findAll());
            return "createPost";
        }
    }

    @GetMapping("/create")
    public String displayPostCreation(Model model) {
        model.addAttribute("post", new PostDTO());
        model.addAttribute("categories", categoryRepository.findAll());
        return "createPost";
    }

    @GetMapping("{id}")
    public String displayPost(@PathVariable Long id, Model model) {
        try {
            Post post = postService.getPostById(id);
            boolean canEditPost = postService.canUserEditPost(id);
            model.addAttribute("post", post);
            model.addAttribute("newComment", new CommentDTO());
            model.addAttribute("canEdit", canEditPost);
            return "viewPost";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:" + HOME;
        }
    }

    @GetMapping("edit/{id}")
    public String editPost(@PathVariable Long id, Model model) {
        try {
            ResponsePostDTO post = postService.getPostForEditing(id);
            model.addAttribute("post", post);
            model.addAttribute("categories", categoryRepository.findAll());
            return "editPost";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:" + HOME;
        }
    }

    @PutMapping("edit/{id}")
    public String editPost(@PathVariable Long id, @ModelAttribute PostDTO postDTO, Model model) {
        postService.editPost(id, postDTO);
        return "redirect:/post/" + id;
    }
}
