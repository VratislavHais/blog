package com.vhais.blog.controller;

import com.vhais.blog.model.User;
import com.vhais.blog.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/auth")
@AllArgsConstructor
public class UserRestController {
    private final UserService userService;

    @GetMapping("current-user")
    public User getCurrentUser() {
        return userService.getAuthenticatedUser().orElseThrow();
    }
}
