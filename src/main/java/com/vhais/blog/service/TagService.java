package com.vhais.blog.service;

import com.vhais.blog.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {
    Tag saveTag(Tag tag);
    Optional<Tag> getTagByName(String name);
    List<Tag> getAllTags();
}
