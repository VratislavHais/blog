package com.vhais.blog.service;

import com.vhais.blog.model.Post;
import com.vhais.blog.model.Tag;
import com.vhais.blog.repository.PostRepository;
import com.vhais.blog.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Post removeTagFromPost(String tagName, Post post) {
        Tag tag = tagRepository.findByName(tagName).orElseThrow(() -> new EntityNotFoundException("Tag " + tagName + " not found"));

        return removeTagFromPost(tag, post);
    }

    @Override
    public Post removeTagFromPost(Tag tag, Post post) {
        List<Post> posts = tag.getPosts();
        posts.remove(post);
        tag.setPosts(posts);
        tagRepository.save(tag);

        Set<Tag> tags = post.getTags();
        tags.remove(tag);
        post.setTags(tags);
        return postRepository.save(post);
    }

    @Override
    public Post addTagsToPost(Post post, String... tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            tags.addAll(
                    Arrays.stream(tagName.split(" "))
                            .map(name -> tagRepository.findByName(name).orElseGet(() -> tagRepository.save(new Tag(name))))
                            .collect(Collectors.toSet())
            );
        }
        return addTagsToPost(post, tags.toArray(Tag[]::new));
    }

    @Override
    public Post addTagsToPost(Post post, Tag... tags) {
        Set<Tag> postTags = post.getTags();
        for (Tag tag : tags) {
            updateListsOfPostAndTag(post, tag);
            postTags.add(tag);
        }
        post.setTags(postTags);
        return postRepository.save(post);
    }

    private void updateListsOfPostAndTag(Post post, Tag tag) {
        Tag fetchedTag = tagRepository.findById(tag.getId()).orElseThrow(() -> new EntityNotFoundException("Tag with name " + tag.getName() + " not found"));
        List<Post> tagPosts = fetchedTag.getPosts();
        tagPosts.add(post);
        fetchedTag.setPosts(tagPosts);
        tagRepository.save(fetchedTag);
    }
}
