package com.vhais.blog.controller;

import com.vhais.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private static final String HOME = "home";
    private final PostService postService;

    @GetMapping("/" + HOME)
    public String home(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "home";
    }

    @GetMapping("/")
    public String redirectHome(Model model) {
        return "redirect:/" + HOME;
    }
}
