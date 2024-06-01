package com.vhais.blog.repository;

import com.vhais.blog.model.Category;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor_Username(String username);
    List<Post> findByCategory_Name(String categoryName);
    List<Post> findByTags_Name(String tagName);
}
