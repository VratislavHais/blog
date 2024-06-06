package com.vhais.blog.service;

import com.vhais.blog.dto.PostDTO;
import com.vhais.blog.dto.ResponsePostDTO;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.Tag;

import java.util.List;

public interface PostService {
    Post savePost(PostDTO post);
    List<Post> getAllPosts();
    List<Post> getPostsByCategoryName(String categoryName);
    List<Post> getPostsByAuthorUsername(String username);
    List<Post> getPostsByTagName(String tagName);
    Post getPostById(Long id);
    ResponsePostDTO getPostForEditing(Long id);
    Post editPost(Long id, PostDTO postDTO);
}
