package com.vhais.blog.controller;

import com.vhais.blog.model.User;
import com.vhais.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/" + REGISTER)
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.saveUser(user);
            return "redirect:" + LOGIN_FULL;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return REGISTER;
        }
    }

    @GetMapping("/" + REGISTER)
    public String showRegistrationForm(Model model) {
        if (isAuthenticated()) {
            return "redirect:" + HOME;
        }
        model.addAttribute("user", new User());
        return REGISTER;
    }

    @GetMapping("/" + LOGIN)
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                Model model) {
        if (isAuthenticated()) {
            return "redirect:" + HOME;
        }
        model.addAttribute("error", error != null);
        return LOGIN;
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String));
    }
}
