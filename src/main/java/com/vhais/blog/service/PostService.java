package com.vhais.blog.service;

import com.vhais.blog.model.Post;
import com.vhais.blog.model.Tag;

import java.util.List;

public interface PostService {
    Post savePost(Post post);
    List<Post> getAllPosts();
    List<Post> getPostsByCategoryName(String categoryName);
    List<Post> getPostsByAuthorUsername(String username);
    List<Post> getPostsByTagName(String tagName);
    Post getPostById(Long id);
}
