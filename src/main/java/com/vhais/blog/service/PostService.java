package com.vhais.blog.service;

import com.vhais.blog.model.Category;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.Tag;
import com.vhais.blog.model.User;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post savePost(Post post);
    List<Post> getAllPosts();
    List<Post> getPostsByCategory(Category category);
    List<Post> getPostsByAuthor(User user);
    List<Post> getPostsByTagName(String tagName);
    Post getPostById(Long id);
    Post getPostById(String id);
}
