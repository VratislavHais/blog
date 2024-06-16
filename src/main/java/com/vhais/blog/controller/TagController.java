package com.vhais.blog.controller;

import com.vhais.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final TagService tagService;

    @GetMapping(TAGS_DELETE)
    @PreAuthorize("@postService.canUserEditPost(#postId)")
    public String deleteTagFromPost(@PathVariable("id") Long postId,
                                    @PathVariable("tagId") Long tagId) {
        tagService.removeTagFromPost(tagId, postId);
        return "redirect:/post/" + postId;
    }

    @PostMapping(TAGS_ADD)
    @PreAuthorize("@postService.canUserEditPost(#postId)")
    public String addTagsToPost(@PathVariable("id") Long postId,
                                @RequestBody String tags) {
        tagService.addTagsToPost(postId, StringUtils.substringAfter(tags, "=").split("\\+"));
        return "redirect:/post/" + postId;
    }
}
