package com.vhais.blog.repository;

import com.vhais.blog.model.Comment;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    List<Comment> findByPost_Id(Long postId);
    List<Comment> findByAuthor(User author);
    List<Comment> findByAuthor_Id(Long authorId);
    List<Comment> findByAuthor_Username(String username);
}
