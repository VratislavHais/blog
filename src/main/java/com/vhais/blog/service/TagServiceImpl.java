package com.vhais.blog.service;

import com.vhais.blog.model.Tag;
import com.vhais.blog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository repository;

    @Override
    public Tag saveTag(Tag tag) {
        return repository.save(tag);
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
