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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@org.junit.jupiter.api.Tag("integration")
public class PostServiceIT {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostService postService;

    private Category category;
    private User user;
    private Tag tag1;
    private Tag tag2;

    @BeforeEach
    public void setup() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        tagRepository.deleteAll();
        category = categoryRepository.save(new Category("test"));
        User testUser = new User();
        testUser.setUsername("test");
        testUser.setEmail("hais@test.cz");
        testUser.setPassword("test");
        user = userRepository.save(testUser);
        tag1 = tagRepository.save(new Tag("1"));
        tag2 = tagRepository.save(new Tag("2"));
    }

    @Test
    public void testSavingPost_thenRetrievingCategoryIfPostIsPresent() {
        Post post = new Post();
        post.setAuthor(user);
        post.setCategory(category);
        post.setTitle("test");
        post.setContent("test");

        Post savedPost = postService.savePost(post);

        Optional<Category> retrievedCategory = categoryRepository.findById(category.getId());
        assertThat(retrievedCategory).isNotEmpty();
        assertThat(retrievedCategory.get().getPosts()).contains(savedPost);
    }

    @Test
    public void testSavingPost_thenRetrievingUserIfPostIsPresent() {
        Post post = new Post();
        post.setAuthor(user);
        post.setCategory(category);
        post.setTitle("test");
        post.setContent("test");

        Post savedPost = postService.savePost(post);

        Optional<User> retrievedUser = userRepository.findById(user.getId());
        assertThat(retrievedUser).isNotEmpty();
        assertThat(retrievedUser.get().getPosts()).contains(savedPost);
    }

    @Test
    public void testSavingPost_thenRetrievingTagsIfPostIsPresent() {
        Post post = new Post();
        post.setAuthor(user);
        post.setCategory(category);
        post.setTitle("test");
        post.setContent("test");
        post.setTags(Set.of(tag1, tag2));

        Post savedPost = postService.savePost(post);

        for (Tag tag : List.of(tag1, tag2)) {
            Optional<Tag> retrievedTag = tagRepository.findById(tag.getId());
            assertThat(retrievedTag).isNotEmpty();
            assertThat(retrievedTag.get().getPosts()).contains(savedPost);
        }
    }
}
