package com.vhais.blog.service;

import com.vhais.blog.model.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagService {
    Tag saveTag(Tag tag);
    Set<Tag> saveAllTags(Set<String> tagNames);
    Optional<Tag> getTagByName(String name);
    List<Tag> getAllTags();
}
