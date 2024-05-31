package com.vhais.blog.service;

import com.vhais.blog.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    User saveUser(User user);

    @Override
    User loadUserByUsername(String username) throws UsernameNotFoundException;
}
