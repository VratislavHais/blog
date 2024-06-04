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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment saveCommentUnderPost(CommentDTO commentDTO, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post with id " + postId + " not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User author = userRepository.findByUsername(authentication.getName()).get();

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
    public void deleteComment(Long commentId, Long postId, String username) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment with id " + commentId + " not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post with id " + postId + " not found"));
        User author = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User with id " + postId + " not found"));
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
}
