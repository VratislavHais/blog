package com.vhais.blog.controller;

import com.vhais.blog.model.Post;
import com.vhais.blog.model.Tag;
import com.vhais.blog.repository.PostRepository;
import com.vhais.blog.service.TagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/post/")
@RequiredArgsConstructor
public class TagController {
    private static final String TAGS = "{id}/tags/";
    private static final String TAGS_DELETE = TAGS + "{tagId}/delete";
    private static final String TAGS_ADD = TAGS + "add";

    private final PostRepository postRepository;
    private final TagService tagService;

    @GetMapping(TAGS_DELETE)
    public String deleteTagFromPost(@PathVariable("id") Long postId,
                                    @PathVariable("tagId") Long tagId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post with id " + postId + " not found"));
        Tag tag = tagService.getTagById(tagId).orElseThrow(() -> new EntityNotFoundException("Tag with id " + tagId + " not found"));
        tagService.removeTagFromPost(tag, post);
        return "redirect:/post/" + postId;
    }

    @PostMapping(TAGS_ADD)
    public String addTagsToPost(@PathVariable("id") Long postId,
                                @RequestBody String tags) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post with id " + postId + " not found"));
        tagService.addTagsToPost(post, StringUtils.substringAfter(tags, "=").split("\\+"));
        return "redirect:/post/" + postId;
    }
}
