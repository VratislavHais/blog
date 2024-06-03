package com.vhais.blog.service;

import com.vhais.blog.dto.PostDTO;
import com.vhais.blog.model.Category;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.Tag;
import com.vhais.blog.model.User;
import com.vhais.blog.repository.CategoryRepository;
import com.vhais.blog.repository.PostRepository;
import com.vhais.blog.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TagService tagService;

    @Override
    @Transactional
    public Post savePost(PostDTO postDTO) {
        Post post = new Post();
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        User user = retrieveAuthor();
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

    private User retrieveAuthor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName()).orElseThrow();
    }

    private Set<String> parseTagField(String tags) {
        if (tags != null) {
            return Arrays.stream(tags.split(" ")).collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    private void updateListOfCategory(Post post) {
        Category category = categoryRepository.findById(post.getCategory().getId()).orElseThrow(() -> new EntityNotFoundException("Category " + post.getCategory().getName() + " does not exist"));
        List<Post> categoryPosts = category.getPosts();
        categoryPosts.add(post);
        category.setPosts(categoryPosts);
        categoryRepository.save(category);
    }

    private void updateListOfUser(Post post) {
        User user = userRepository.findById(post.getAuthor().getId()).orElseThrow(() -> new EntityNotFoundException("User with username " + post.getAuthor().getUsername() + " not found"));
        List<Post> userPosts = user.getPosts();
        userPosts.add(post);
        user.setPosts(userPosts);
        userRepository.save(user);
    }

    private void updateListOfTags(Post post) {
        // need to create new HashSet due to concurrency issue
        for (Tag tag : new HashSet<>(post.getTags())) {
            updateListOfTag(post, tag);
        }
    }

    private void updateListOfTag(Post post, Tag tag) {
        Tag fetchedTag = tagService.getTagById(tag.getId()).orElseThrow(() -> new EntityNotFoundException("Tag with name " + tag.getName() + " not found"));
        List<Post> tagPosts = fetchedTag.getPosts();
        tagPosts.add(post);
        fetchedTag.setPosts(tagPosts);
        tagService.saveTag(fetchedTag);
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
}
