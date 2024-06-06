package com.vhais.blog.service;

import com.vhais.blog.dto.UserDTO;
import com.vhais.blog.model.User;
import com.vhais.blog.repository.RoleRepository;
import com.vhais.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setUsername(userDTO.getUsername());
        user.setRoles(Set.of(roleRepository.findByName("User")));
        return userRepository.save(user);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> new UsernameNotFoundException(String.format("User with username %s not found.", username)));
    }

    @Override
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.isAuthenticated() && (authentication.getPrincipal() instanceof UserDetails));
    }

    @Override
    public Optional<User> getAuthenticatedUser() {
        Optional<User> result = Optional.empty();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            result = userRepository.findByUsername(authentication.getName());
        }
        return result;
    }
}
