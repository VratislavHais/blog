package com.vhais.blog.service;

import com.vhais.blog.model.Post;
import com.vhais.blog.model.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagService {
    Tag saveTag(Tag tag);
    Set<Tag> saveAllTags(Set<String> tagNames);
    Optional<Tag> getTagByName(String name);
    Optional<Tag> getTagById(Long id);
    List<Tag> getAllTags();
    void removeTagFromPost(Long tagId, Long postId);
    void addTagsToPost(Long postId, String... tagNames);
}
