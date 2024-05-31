package com.vhais.blog.dto;

import lombok.Data;

@Data
public class PostDTO {
    private String title;
    private String content;
    private String tags;
    private String category;
}
