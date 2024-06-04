package com.vhais.blog.controller;

import com.vhais.blog.dto.UserDTO;
import com.vhais.blog.model.User;
import com.vhais.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
    private static final String HOME = "/home";
    private static final String REGISTER = "register";
    private static final String REGISTER_FULL = "/auth/register";
    private static final String LOGIN = "login";
    private static final String LOGIN_FULL = "/auth/login";

    private final UserService userService;

    @PostMapping("/" + REGISTER)
    public String registerUser(@ModelAttribute UserDTO userDTO, Model model) {
        try {
            userService.saveUser(userDTO);
            return "redirect:" + LOGIN_FULL;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return REGISTER;
        }
    }

    @GetMapping("/" + REGISTER)
    public String showRegistrationForm(Model model) {
        if (userService.isAuthenticated()) {
            return "redirect:" + HOME;
        }
        model.addAttribute("user", new UserDTO());
        return REGISTER;
    }

    @GetMapping("/" + LOGIN)
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                Model model) {
        if (userService.isAuthenticated()) {
            return "redirect:" + HOME;
        }
        model.addAttribute("error", error != null);
        return LOGIN;
    }
}
