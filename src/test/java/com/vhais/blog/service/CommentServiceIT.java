package com.vhais.blog.service;

import com.vhais.blog.dto.CommentDTO;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
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
    @WithMockUser(username = "test")
    public void testCommentIncludedInUserList() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent("test");
        Comment comment = commentService.saveCommentUnderPost(commentDTO, post.getId());

        User fetched = userRepository.findById(user.getId()).get();
        assertThat(fetched.getComments()).contains(comment);
    }

    @Test
    @WithMockUser(username = "test")
    public void testCommentIncludedInPostList() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent("test");
        Comment comment = commentService.saveCommentUnderPost(commentDTO, post.getId());

        Post fetched = postRepository.findById(post.getId()).get();
        assertThat(fetched.getComments()).contains(comment);
    }

    @Test
    @WithMockUser(username = "test")
    public void testCommentStoredWithAllData() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent("test");
        Comment comment = commentService.saveCommentUnderPost(commentDTO, post.getId());

        assertThat(comment.getAuthor()).isEqualTo(user);
        assertThat(comment.getPost()).isEqualTo(post);
        assertThat(comment.getContent()).isEqualTo("test");
        assertThat(comment.getCreatedAt()).isNotNull();
    }

    @Test
    @WithMockUser(username = "test")
    public void testCommandRemovedFromUserAndPostLists() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent("test");
        Comment comment = commentService.saveCommentUnderPost(commentDTO, post.getId());

        commentService.deleteComment(comment.getId(), post.getId(), user.getUsername());

        user = userRepository.findById(user.getId()).get();
        assertThat(user.getComments()).doesNotContain(comment);

        post = postRepository.findById(post.getId()).get();
        assertThat(post.getComments()).doesNotContain(comment);
    }
}
