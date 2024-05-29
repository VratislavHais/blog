package com.vhais.blog.service;

import com.vhais.blog.model.Comment;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.User;

import java.util.List;

public interface CommentService {
    Comment saveComment(Comment comment);
    List<Comment> getCommentsByPost(Post post);
    List<Comment> getCommentsByAuthor(User user);
}
