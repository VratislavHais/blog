package com.vhais.blog.service;

import com.vhais.blog.dto.PostDTO;
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
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
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

    @Autowired
    private MockMvc mockMvc;

    private Category category;
    private User user;
    private Tag tag1;
    private Tag tag2;
    private Post post;

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

        PostDTO testPost = new PostDTO();
        testPost.setCategory(category.getName());
        testPost.setTitle("test");
        testPost.setContent("test");
        testPost.setTags(tag1.getName() + " " + tag2.getName());
        post = postService.savePost(testPost);
    }

    @Test
    @WithMockUser(username = "test")
    public void testSavingPost_thenRetrievingCategoryIfPostIsPresent() {
        Optional<Category> retrievedCategory = categoryRepository.findById(category.getId());
        assertThat(retrievedCategory).isNotEmpty();
        assertThat(retrievedCategory.get().getPosts()).contains(post);
    }

    @Test
    @WithMockUser(username = "test")
    public void testSavingPost_thenRetrievingUserIfPostIsPresent() {
        Optional<User> retrievedUser = userRepository.findById(user.getId());
        assertThat(retrievedUser).isNotEmpty();
        assertThat(retrievedUser.get().getPosts()).contains(post);
    }

    @Test
    @WithMockUser(username = "test")
    public void testSavingPost_thenRetrievingTagsIfPostIsPresent() {
        for (Tag tag : Set.of(tag1, tag2)) {
            Optional<Tag> retrievedTag = tagRepository.findById(tag.getId());
            assertThat(retrievedTag).isNotEmpty();
            assertThat(retrievedTag.get().getPosts()).contains(post);
        }
    }

    @Test
    @WithMockUser(username = "test")
    public void testGettingPostById_whenPostExists() {
        Post retrieved = postService.getPostById(post.getId());
        assertThat(retrieved).isEqualTo(post);
    }

    @Test
    @WithMockUser(username = "test")
    public void testGettingPostById_whenPostDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .describedAs("Post with id " + (post.getId() + 1) + " not found")
                .isThrownBy(() -> postService.getPostById(post.getId() + 1));
    }

    @Test
    @WithMockUser(username = "test")
    public void testGettingPostsByCategoryName_whenCategoryHasPosts() {
        List<Post> posts = postService.getPostsByCategoryName(category.getName());
        assertThat(posts).isNotEmpty();
        assertThat(posts).contains(post);
    }

    @Test
    @WithMockUser(username = "test")
    public void testGettingPostsByCategoryName_whenCategoryHasNoPosts() {
        Category newCategory = new Category("new");
        categoryRepository.save(newCategory);
        List<Post> posts = postService.getPostsByCategoryName(newCategory.getName());
        assertThat(posts).isEmpty();
    }

    @Test
    @WithMockUser(username = "test")
    public void testGettingPostsByCategoryName_whenCategoryHasMultiplePosts() {
        List<Post> posts = createMultiplePosts(5);
        posts.add(post);

        List<Post> retrieved = postService.getPostsByCategoryName(category.getName());
        assertThat(retrieved).hasSize(posts.size());
        assertThat(retrieved).hasSameElementsAs(posts);
    }

    @Test
    @WithMockUser(username = "test")
    public void testGettingPostsByUsername_whenUserHasPosts() {
        List<Post> posts = postService.getPostsByAuthorUsername(user.getUsername());
        assertThat(posts).isNotEmpty();
        assertThat(posts).contains(post);
    }

    @Test
    @WithMockUser(username = "test")
    public void testGettingPostsByUsername_whenUserHasNoPosts() {
        User newUser = new User();
        newUser.setUsername("new");
        newUser.setPassword("new");
        newUser.setEmail("new@test.cz");
        userRepository.save(newUser);
        List<Post> posts = postService.getPostsByAuthorUsername(newUser.getUsername());
        assertThat(posts).isEmpty();
    }

    @Test
    @WithMockUser(username = "test")
    public void testGettingPostsByUsername_whenUserHasMultiplePosts() {
        List<Post> posts = createMultiplePosts(5);
        posts.add(post);

        List<Post> retrieved = postService.getPostsByAuthorUsername(user.getUsername());
        assertThat(retrieved).hasSize(posts.size());
        assertThat(retrieved).hasSameElementsAs(posts);
    }

    @Test
    @WithMockUser(username = "test")
    public void testGettingPostsByTagName_whenTagsHavePosts() {
        for (Tag tag : Set.of(tag1, tag2)) {
            List<Post> posts = postService.getPostsByTagName(tag.getName());
            assertThat(posts).isNotEmpty();
            assertThat(posts).contains(post);
        }
    }

    @Test
    @WithMockUser(username = "test")
    public void testGettingPostsByTagName_whenTagHasNoPosts() {
        Tag newTag = new Tag("newTag");
        tagRepository.save(newTag);
        List<Post> posts = postService.getPostsByTagName(newTag.getName());
        assertThat(posts).isEmpty();
    }

    @Test
    @WithMockUser(username = "test")
    public void testGettingPostsByTagName_whenTagsHaveMultiplePosts() {
        List<Post> posts = createMultiplePosts(5, tag1, tag2);
        posts.add(post);

        for (Tag tag : Set.of(tag1, tag2)) {
            List<Post> retrieved = postService.getPostsByTagName(tag.getName());
            assertThat(retrieved).hasSize(posts.size());
            assertThat(retrieved).hasSameElementsAs(posts);
        }
    }

    @Test
    @WithMockUser(username = "test")
    public void testGettingPostsByTagName_whenOneHasPostsAndSecondDoNot() {
        List<Post> posts = createMultiplePosts(5, tag1);

        // tag1
        List<Post> postsTag1 = postService.getPostsByTagName(tag1.getName());
        // +1 due to post global variable
        assertThat(postsTag1).hasSize(posts.size() + 1);
        assertThat(postsTag1).containsAll(postsTag1);
        assertThat(postsTag1).contains(post);

        // tag2
        List<Post> postsTag2 = postService.getPostsByTagName(tag2.getName());
        // only global post should be present
        assertThat(postsTag2).hasSize(1);
        assertThat(postsTag2).containsOnly(post);
    }

    private List<Post> createMultiplePosts(int n, Tag... tags) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            PostDTO newPost = new PostDTO();
            newPost.setCategory(category.getName());
            newPost.setTitle("title" + i);
            newPost.setContent("content" + i);
            newPost.setTags(String.join(" ", Arrays.stream(tags).map(Tag::getName).reduce("", (first, second) -> first + " " + second)));
            posts.add(postService.savePost(newPost));
        }
        return posts;
    }
}
