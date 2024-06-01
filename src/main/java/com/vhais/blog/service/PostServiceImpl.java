package com.vhais.blog.service;

import com.vhais.blog.model.Category;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.Tag;
import com.vhais.blog.model.User;
import com.vhais.blog.repository.CategoryRepository;
import com.vhais.blog.repository.PostRepository;
import com.vhais.blog.repository.TagRepository;
import com.vhais.blog.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        Tag fetchedTag = tagRepository.findById(tag.getId()).orElseThrow(() -> new EntityNotFoundException("Tag with name " + tag.getName() + " not found"));
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
    public Post removeTagFromPost(String tagName, Post post) {
        Tag tag = tagRepository.findByName(tagName).orElseThrow(() -> new EntityNotFoundException("Tag " + tagName + " not found"));

        return removeTagFromPost(tag, post);
    }

    @Override
    public Post removeTagFromPost(Tag tag, Post post) {
        List<Post> posts = tag.getPosts();
        posts.remove(post);
        tag.setPosts(posts);
        tagRepository.save(tag);

        Set<Tag> tags = post.getTags();
        tags.remove(tag);
        post.setTags(tags);
        return postRepository.save(post);
    }

    @Override
    public Post addTagsToPost(Post post, String... tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            tags.addAll(
                    Arrays.stream(tagName.split(" "))
                            .map(name -> tagRepository.findByName(name).orElse(tagRepository.save(new Tag(name))))
                            .collect(Collectors.toSet())
            );
        }
        return addTagsToPost(post, tags.toArray(Tag[]::new));
    }

    @Override
    public Post addTagsToPost(Post post, Tag... tags) {
        Set<Tag> postTags = post.getTags();
        for (Tag tag : tags) {
            updateListOfTag(post, tag);
            postTags.add(tag);
        }
        post.setTags(postTags);
        return postRepository.save(post);
    }
}
