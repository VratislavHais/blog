package com.vhais.blog.service;

import com.vhais.blog.model.Comment;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.User;
import com.vhais.blog.repository.CommentRepository;
import com.vhais.blog.repository.PostRepository;
import com.vhais.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public Comment saveComment(Comment comment, Post post, User user) {
        Comment savedComment =  commentRepository.save(comment);

        updateCommentListOfPost(savedComment, post);
        updateCommentListOfUser(savedComment, user);

        return savedComment;
    }

    private void updateCommentListOfPost(Comment comment, Post post) {
        List<Comment> comments = post.getComments();
        comments.add(comment);
        post.setComments(comments);
        postRepository.save(post);
    }

    private void updateCommentListOfUser(Comment comment, User user) {
        List<Comment> comments = user.getComments();
        comments.add(comment);
        user.setComments(comments);
        userRepository.save(user);
    }

    @Override
    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findByPost(post);
    }

    @Override
    public List<Comment> getCommentsByAuthor(User user) {
        return commentRepository.findByAuthor(user);
    }
}
