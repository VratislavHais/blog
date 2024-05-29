package com.vhais.blog.service;

import com.vhais.blog.model.Comment;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.User;
import com.vhais.blog.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;

    @Override
    public Comment saveComment(Comment comment) {
        return repository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByPost(Post post) {
        return repository.findByPost(post);
    }

    @Override
    public List<Comment> getCommentsByAuthor(User user) {
        return repository.findByAuthor(user);
    }
}
