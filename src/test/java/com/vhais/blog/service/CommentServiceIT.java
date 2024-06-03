package com.vhais.blog.service;

import com.vhais.blog.model.Category;
import com.vhais.blog.model.Comment;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.User;
import com.vhais.blog.repository.CategoryRepository;
import com.vhais.blog.repository.CommentRepository;
import com.vhais.blog.repository.PostRepository;
import com.vhais.blog.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Tag("integration")
public class CommentServiceIT {
    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Post post;
    private User user;

    @BeforeEach
    public void setup() {
        commentRepository.deleteAll();

        user = new User();
        user.setEmail("hais@test.cz");
        user.setUsername("test");
        user.setPassword("test");
        user = userRepository.save(user);

        Category category = categoryRepository.save(new Category("test"));

        post = new Post();
        post.setAuthor(user);
        post.setCategory(category);
        post.setContent("test");
        post.setTitle("test");
        post = postRepository.save(post);
    }

    @Test
    public void testCommentIncludedInUserList() {
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(user);
        comment.setContent("test");
        comment = commentService.saveComment(comment, post, user);

        assertThat(user.getComments()).contains(comment);
    }

    @Test
    public void testCommentIncludedInPostList() {
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(user);
        comment.setContent("test");
        comment = commentService.saveComment(comment, post, user);

        assertThat(post.getComments()).contains(comment);
    }

    @Test
    public void testCommandRemovedFromUserAndPostLists() {
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(user);
        comment.setContent("test");
        comment = commentService.saveComment(comment, post, user);

        commentService.deleteComment(comment, post, user);

        user = userRepository.findById(user.getId()).get();
        assertThat(user.getComments()).doesNotContain(comment);

        post = postRepository.findById(post.getId()).get();
        assertThat(post.getComments()).doesNotContain(comment);
    }
}
