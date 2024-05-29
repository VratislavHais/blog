package com.vhais.blog.service;

import com.vhais.blog.model.Category;
import com.vhais.blog.model.Post;
import com.vhais.blog.model.User;
import com.vhais.blog.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository repository;

    @Override
    public Post savePost(Post post) {
        return repository.save(post);
    }

    @Override
    public List<Post> getAllPosts() {
        return repository.findAll();
    }

    @Override
    public List<Post> getPostsByCategory(Category category) {
        return repository.findByCategory(category);
    }

    @Override
    public List<Post> getPostsByAuthor(User user) {
        return repository.findByAuthor(user);
    }

    @Override
    public List<Post> getPostsByTagName(String tagName) {
        return repository.findByTags_Name(tagName);
    }

    @Override
    @Transactional
    public Post getPostById(Long id) {
        return repository.getReferenceById(id);
    }

    @Override
    public Post getPostById(String id) throws NumberFormatException {
        Long idLong = Long.parseLong(id);
        return getPostById(idLong);
    }
}
