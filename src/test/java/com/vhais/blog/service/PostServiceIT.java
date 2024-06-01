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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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

        Post testPost = new Post();
        testPost.setAuthor(user);
        testPost.setCategory(category);
        testPost.setTitle("test");
        testPost.setContent("test");
        testPost.setTags(Set.of(tag1, tag2));
        post = postService.savePost(testPost);
    }

    @Test
    public void testSavingPost_thenRetrievingCategoryIfPostIsPresent() {
        Optional<Category> retrievedCategory = categoryRepository.findById(category.getId());
        assertThat(retrievedCategory).isNotEmpty();
        assertThat(retrievedCategory.get().getPosts()).contains(post);
    }

    @Test
    public void testSavingPost_thenRetrievingUserIfPostIsPresent() {
        Optional<User> retrievedUser = userRepository.findById(user.getId());
        assertThat(retrievedUser).isNotEmpty();
        assertThat(retrievedUser.get().getPosts()).contains(post);
    }

    @Test
    public void testSavingPost_thenRetrievingTagsIfPostIsPresent() {
        for (Tag tag : Set.of(tag1, tag2)) {
            Optional<Tag> retrievedTag = tagRepository.findById(tag.getId());
            assertThat(retrievedTag).isNotEmpty();
            assertThat(retrievedTag.get().getPosts()).contains(post);
        }
    }

    @Test
    public void testGettingPostById_whenPostExists() {
        Post retrieved = postService.getPostById(post.getId());
        assertThat(retrieved).isEqualTo(post);
    }

    @Test
    public void testGettingPostById_whenPostDoesNotExist() {
        assertThatExceptionOfType(EntityNotFoundException.class)
                .describedAs("Post with id " + (post.getId() + 1) + " not found")
                .isThrownBy(() -> postService.getPostById(post.getId() + 1));
    }

    @Test
    public void testGettingPostsByCategoryName_whenCategoryHasPosts() {
        List<Post> posts = postService.getPostsByCategoryName(category.getName());
        assertThat(posts).isNotEmpty();
        assertThat(posts).contains(post);
    }

    @Test
    public void testGettingPostsByCategoryName_whenCategoryHasNoPosts() {
        Category newCategory = new Category("new");
        categoryRepository.save(newCategory);
        List<Post> posts = postService.getPostsByCategoryName(newCategory.getName());
        assertThat(posts).isEmpty();
    }

    @Test
    public void testGettingPostsByCategoryName_whenCategoryHasMultiplePosts() {
        List<Post> posts = createMultiplePosts(5);
        posts.add(post);

        List<Post> retrieved = postService.getPostsByCategoryName(category.getName());
        assertThat(retrieved).hasSize(posts.size());
        assertThat(retrieved).hasSameElementsAs(posts);
    }

    @Test
    public void testGettingPostsByUsername_whenUserHasPosts() {
        List<Post> posts = postService.getPostsByAuthorUsername(user.getUsername());
        assertThat(posts).isNotEmpty();
        assertThat(posts).contains(post);
    }

    @Test
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
    public void testGettingPostsByUsername_whenUserHasMultiplePosts() {
        List<Post> posts = createMultiplePosts(5);
        posts.add(post);

        List<Post> retrieved = postService.getPostsByAuthorUsername(user.getUsername());
        assertThat(retrieved).hasSize(posts.size());
        assertThat(retrieved).hasSameElementsAs(posts);
    }

    @Test
    public void testGettingPostsByTagName_whenTagsHavePosts() {
        for (Tag tag : Set.of(tag1, tag2)) {
            List<Post> posts = postService.getPostsByTagName(tag.getName());
            assertThat(posts).isNotEmpty();
            assertThat(posts).contains(post);
        }
    }

    @Test
    public void testGettingPostsByTagName_whenTagHasNoPosts() {
        Tag newTag = new Tag("newTag");
        tagRepository.save(newTag);
        List<Post> posts = postService.getPostsByTagName(newTag.getName());
        assertThat(posts).isEmpty();
    }

    @Test
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

    @Test
    public void testRemovingTagFromPost() {
        postService.removeTagFromPost(tag1.getName(), post);

        Post retrievedPost = postService.getPostById(post.getId());
        assertThat(retrievedPost.getTags()).doesNotContain(tag1);
        assertThat(retrievedPost.getTags()).contains(tag2);

        Tag retrievedTag = tagRepository.findById(tag1.getId()).get();
        assertThat(retrievedTag.getPosts()).doesNotContain(post);
    }

    @Test
    public void testAddingNewTagToPostByName() {
        String tagName = "newTagName";
        post = postService.addTagsToPost(post, tagName);

        Optional<Tag> retrievedTag = tagRepository.findByName(tagName);
        assertThat(retrievedTag).isNotEmpty();
        assertThat(retrievedTag.get().getName()).isEqualTo(tagName);
        assertThat(retrievedTag.get().getPosts()).contains(post);

        assertThat(post.getTags()).contains(retrievedTag.get());
    }

    @Test
    public void testAddingNewTagsToPostByNamePassedAsArray() {
        String[] tagNames = new String[]{"newTagName", "anotherTagName"};
        post = postService.addTagsToPost(post, tagNames);

        for (String tagName : tagNames) {
            Optional<Tag> retrievedTag = tagRepository.findByName(tagName);
            assertThat(retrievedTag).isNotEmpty();
            assertThat(retrievedTag.get().getName()).isEqualTo(tagName);
            assertThat(retrievedTag.get().getPosts()).contains(post);

            assertThat(post.getTags()).contains(retrievedTag.get());
        }
    }

    @Test
    public void testAddingNewTagsToPostPassedAsSpaceSeparatedValues() {
        String tagNames = "newTagName anotherTagName";
        post = postService.addTagsToPost(post, tagNames);

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
            posts.add(postService.savePost(newPost));
        }
        return posts;
    }
}
