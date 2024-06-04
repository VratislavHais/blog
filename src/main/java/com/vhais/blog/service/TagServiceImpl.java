package com.vhais.blog.service;

import com.vhais.blog.model.Post;
import com.vhais.blog.model.Tag;
import com.vhais.blog.repository.PostRepository;
import com.vhais.blog.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Set<Tag> saveAllTags(Set<String> tags) {
        Set<Tag> result = new HashSet<>();
        for (String tagName : tags) {
            Tag tag = getTagByName(tagName).orElse(null);
            if (tag == null) {
                tag = tagRepository.save(new Tag(tagName));
            }
            result.add(tag);
        }
        return result;
    }

    @Override
    public Optional<Tag> getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    @Override
    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    @Transactional
    public void removeTagFromPost(Long tagId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post with id " + postId + " not found"));
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new EntityNotFoundException("Tag " + tagId + " not found"));
        tag.removePost(post);
        post.removeTag(tag);
    }

    @Override
    @Transactional
    public void addTagsToPost(Long postId, String... tagNames) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post with id " + postId + " not found"));
        for (String tagName : tagNames) {
                Arrays.stream(tagName.split(" "))
                        .map(name -> tagRepository.findByName(name).orElseGet(() -> tagRepository.save(new Tag(name))))
                        .forEach(tag -> {
                            tag.addPost(post);
                            post.addTag(tag);
                        });
        }
    }
}
