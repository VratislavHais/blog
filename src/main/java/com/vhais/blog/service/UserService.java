package com.vhais.blog.service;

import com.vhais.blog.dto.UserDTO;
import com.vhais.blog.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService extends UserDetailsService {
    User saveUser(UserDTO userDTO);

    @Override
    User loadUserByUsername(String username) throws UsernameNotFoundException;

    boolean isAuthenticated();

    Optional<User> getAuthenticatedUser();
}
