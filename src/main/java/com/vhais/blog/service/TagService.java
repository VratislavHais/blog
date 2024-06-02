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
    Post removeTagFromPost(String tagName, Post post);
    Post removeTagFromPost(Tag tag, Post post);
    Post addTagsToPost(Post post, String... tagNames);
    Post addTagsToPost(Post post, Tag... tags);
}
