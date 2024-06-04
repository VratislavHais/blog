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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@org.junit.jupiter.api.Tag("integration")
public class TagServiceIT {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagService tagService;

    private Post post;
    private Tag tag1;
    private Tag tag2;
    private Category category;
    private User user;

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

        Post testPost = new Post();
        testPost.setAuthor(user);
        testPost.setCategory(category);
        testPost.setTitle("test");
        testPost.setContent("test");
        testPost.setTags(Set.of(tag1, tag2));
        post = postRepository.save(testPost);
    }

    @Test
    @WithMockUser(username = "test")
    public void testRemovingTagFromPost() {
        tagService.removeTagFromPost(tag1.getId(), post.getId());

        Post retrievedPost = postRepository.findById(post.getId()).get();
        assertThat(retrievedPost.getTags()).doesNotContain(tag1);
        assertThat(retrievedPost.getTags()).contains(tag2);

        Tag retrievedTag = tagService.getTagById(tag1.getId()).get();
        assertThat(retrievedTag.getPosts()).doesNotContain(post);
    }

    @Test
    @WithMockUser(username = "test")
    public void testAddingNewTagToPostByName() {
        String tagName = "newTagName";
        tagService.addTagsToPost(post.getId(), tagName);

        post = postRepository.findById(post.getId()).get();
        Optional<Tag> retrievedTag = tagRepository.findByName(tagName);
        assertThat(retrievedTag).isNotEmpty();
        assertThat(retrievedTag.get().getName()).isEqualTo(tagName);
        assertThat(retrievedTag.get().getPosts()).contains(post);

        assertThat(post.getTags()).contains(retrievedTag.get());
    }

    @Test
    @WithMockUser(username = "test")
    public void testAddingNewTagsToPostByNamePassedAsArray() {
        String[] tagNames = new String[]{"newTagName", "anotherTagName"};
        tagService.addTagsToPost(post.getId(), tagNames);

        post = postRepository.findById(post.getId()).get();

        for (String tagName : tagNames) {
            Optional<Tag> retrievedTag = tagRepository.findByName(tagName);
            assertThat(retrievedTag).isNotEmpty();
            assertThat(retrievedTag.get().getName()).isEqualTo(tagName);
            assertThat(retrievedTag.get().getPosts()).contains(post);

            assertThat(post.getTags()).contains(retrievedTag.get());
        }
    }

    @Test
    @WithMockUser(username = "test")
    public void testAddingNewTagsToPostPassedAsSpaceSeparatedValues() {
        String tagNames = "newTagName anotherTagName";
        tagService.addTagsToPost(post.getId(), tagNames);

        post = postRepository.findById(post.getId()).get();

        for (String tagName : tagNames.split(" ")) {
            Optional<Tag> retrievedTag = tagRepository.findByName(tagName);
            assertThat(retrievedTag).isNotEmpty();
            assertThat(retrievedTag.get().getName()).isEqualTo(tagName);
            assertThat(retrievedTag.get().getPosts()).contains(post);

            assertThat(post.getTags()).contains(retrievedTag.get());
        }
    }

    private List<Post> createMultiplePosts(int n, Tag... tags) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Post newPost = new Post();
            newPost.setCategory(category);
            newPost.setAuthor(user);
            newPost.setTitle("title" + i);
            newPost.setContent("content" + i);
            newPost.setTags(Set.of(tags));
            posts.add(postRepository.save(newPost));
        }
        return posts;
    }
}
