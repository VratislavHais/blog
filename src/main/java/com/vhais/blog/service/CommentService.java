package com.vhais.blog.service;

import com.vhais.blog.dto.CommentDTO;
import com.vhais.blog.model.Comment;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.User;

import java.util.List;

public interface CommentService {
    Comment saveComment(Comment comment);
    Comment saveCommentUnderPost(CommentDTO commentDTO, Long postId);
    void deleteComment(Long commentId, Long postId);
    List<Comment> getCommentsByPost(Long postId);
    List<Comment> getCommentsByAuthor(Long userId);
    List<Comment> getCommentsByUsername(String username);
    boolean canUserEditComment(Long commentId);
}
