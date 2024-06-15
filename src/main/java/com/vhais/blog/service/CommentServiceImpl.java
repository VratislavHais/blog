package com.vhais.blog.service;

import com.vhais.blog.dto.CommentDTO;
import com.vhais.blog.model.Comment;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.User;
import com.vhais.blog.repository.CommentRepository;
import com.vhais.blog.repository.PostRepository;
import com.vhais.blog.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service("commentService")
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment saveCommentUnderPost(CommentDTO commentDTO, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post with id " + postId + " not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User author = userService.getAuthenticatedUser().orElseThrow();

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);

        post.addComment(saved);
        author.addComment(saved);

        return saved;
    }

    @Override
    @Transactional
    @PreAuthorize("@commentService.canUserEditComment(#commentId)")
    public void deleteComment(Long commentId, Long postId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment with id " + commentId + " not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post with id " + postId + " not found"));
        User author = userService.loadUserByUsername(comment.getAuthor().getUsername());
        post.removeComment(comment);
        author.removeComment(comment);

        commentRepository.delete(comment);
    }

    @Override
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPost_Id(postId);
    }

    @Override
    public List<Comment> getCommentsByAuthor(Long userId) {
        return commentRepository.findByAuthor_Id(userId);
    }

    @Override
    public List<Comment> getCommentsByUsername(String username) {
        return commentRepository.findByAuthor_Username(username);
    }

    @Override
    public boolean canUserEditComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        Optional<User> user = userService.getAuthenticatedUser();
        return (user.isPresent() && ("ROLE_ADMIN".equals(user.get().getRole()) || comment.getAuthor() == user.get()));
    }
}
