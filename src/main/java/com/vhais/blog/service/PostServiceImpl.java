package com.vhais.blog.service;

import com.vhais.blog.model.Category;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.Tag;
import com.vhais.blog.model.User;
import com.vhais.blog.repository.CategoryRepository;
import com.vhais.blog.repository.PostRepository;
import com.vhais.blog.repository.TagRepository;
import com.vhais.blog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public Post savePost(Post post) {
        Post savedPost = postRepository.save(post);

        updateListOfCategory(savedPost);
        updateListOfUser(savedPost);
        updateListOfTags(savedPost);

        return savedPost;
    }

    private void updateListOfCategory(Post post) {
        Category category = categoryRepository.findById(post.getCategory().getId()).orElseThrow(() -> new IllegalArgumentException("Category " + post.getCategory().getName() + " does not exist"));
        List<Post> categoryPosts = category.getPosts();
        categoryPosts.add(post);
        category.setPosts(categoryPosts);
        categoryRepository.save(category);
    }

    private void updateListOfUser(Post post) {
        User user = userRepository.findById(post.getAuthor().getId()).orElseThrow(() -> new IllegalArgumentException("User with username " + post.getAuthor().getUsername() + " not found"));
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
        Tag fetchedTag = tagRepository.findById(tag.getId()).orElseThrow(() -> new IllegalArgumentException("Tag with name " + tag.getName() + " not found"));
        List<Post> tagPosts = fetchedTag.getPosts();
        tagPosts.add(post);
        fetchedTag.setPosts(tagPosts);
        tagRepository.save(fetchedTag);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> getPostsByCategory(Category category) {
        return postRepository.findByCategory(category);
    }

    @Override
    public List<Post> getPostsByAuthor(User user) {
        return postRepository.findByAuthor(user);
    }

    @Override
    public List<Post> getPostsByTagName(String tagName) {
        return postRepository.findByTags_Name(tagName);
    }

    @Override
    @Transactional
    public Post getPostById(Long id) {
        return postRepository.getReferenceById(id);
    }

    @Override
    public Post getPostById(String id) throws NumberFormatException {
        Long idLong = Long.parseLong(id);
        return getPostById(idLong);
    }
}
