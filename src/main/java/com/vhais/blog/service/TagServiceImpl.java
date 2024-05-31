package com.vhais.blog.service;

import com.vhais.blog.model.Post;
import com.vhais.blog.model.Tag;
import com.vhais.blog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository repository;

    @Override
    public Tag saveTag(Tag tag) {
        return repository.save(tag);
    }

    @Override
    public Set<Tag> saveAllTags(Set<String> tags) {
        Set<Tag> result = new HashSet<>();
        for (String tagName : tags) {
            Tag tag = getTagByName(tagName).orElse(null);
            if (tag == null) {
                tag = new Tag(tagName);
                repository.save(tag);
            }
            result.add(tag);
        }
        return result;
    }

    @Override
    public Optional<Tag> getTagByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public List<Tag> getAllTags() {
        return repository.findAll();
    }
}
