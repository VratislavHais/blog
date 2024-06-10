package com.vhais.blog.service;

import com.vhais.blog.dto.PostDTO;
import com.vhais.blog.dto.ResponsePostDTO;
import com.vhais.blog.model.Category;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.Tag;
import com.vhais.blog.model.User;
import com.vhais.blog.repository.CategoryRepository;
import com.vhais.blog.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service("postService")
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final TagService tagService;

    @Override
    @Transactional
    public Post savePost(PostDTO postDTO) {
        Post post = new Post();

        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        User user = userService.getAuthenticatedUser().orElseThrow();
        post.setAuthor(user);
        Set<Tag> tags = tagService.saveAllTags(parseTagField(postDTO.getTags()));
        post.setTags(tags);
        Category category = categoryRepository.findByName(postDTO.getCategory()).orElseThrow(() -> new EntityNotFoundException("Category " + postDTO.getCategory() + " not found"));
        post.setCategory(category);
        post.setContent(postDTO.getContent());
        post.setTitle(postDTO.getTitle());

        Post savedPost = postRepository.save(post);

        user.addPost(savedPost);
        category.addPost(savedPost);
        tags.forEach(tag -> tag.addPost(savedPost));

        return savedPost;
    }

    private Set<String> parseTagField(String tags) {
        if (tags != null) {
            return Arrays.stream(tags.split(" ")).collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> getPostsByCategoryName(String categoryName) {
        return postRepository.findByCategory_Name(categoryName);
    }

    @Override
    public List<Post> getPostsByAuthorUsername(String username) {
        return postRepository.findByAuthor_Username(username);
    }

    @Override
    public List<Post> getPostsByTagName(String tagName) {
        return postRepository.findByTags_Name(tagName);
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post with id " + id + " not found"));
    }

    @Override
    @PreAuthorize("@postService.canUserEditPost(#id)")
    public ResponsePostDTO getPostForEditing(@P("id") Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post with id " + id + " not found"));
        ResponsePostDTO response = new ResponsePostDTO();
        response.setId(post.getId());
        response.setCategory(post.getCategory().getName());
        response.setContent(post.getContent());
        response.setTitle(post.getTitle());
        response.setTags(post.getTags().stream().map(Tag::getName).reduce("", (a, b) -> a + " " + b).trim());
        return response;
    }

    @Override
    @PreAuthorize("@postService.canUserEditPost(#id)")
    public Post editPost(@P("id") Long id, PostDTO postDTO) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post with id " + id + " not found"));

        post.setUpdatedAt(LocalDateTime.now());
        Set<Tag> tags = tagService.saveAllTags(parseTagField(postDTO.getTags()));
        post.setTags(tags);
        Category category = categoryRepository.findByName(postDTO.getCategory()).orElseThrow(() -> new EntityNotFoundException("Category " + postDTO.getCategory() + " not found"));
        post.setCategory(category);
        post.setContent(postDTO.getContent());
        post.setTitle(postDTO.getTitle());

        return postRepository.save(post);
    }

    @Override
    public boolean canUserEditPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        Optional<User> user = userService.getAuthenticatedUser();
        return (user.isPresent() && ("ROLE_ADMIN".equals(user.get().getRole()) || post.getAuthor() == user.get()));
    }
}
