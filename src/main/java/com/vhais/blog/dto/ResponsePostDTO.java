package com.vhais.blog.dto;

import lombok.Data;

@Data
public class ResponsePostDTO {
    private Long id;
    private String title;
    private String content;
    private String tags;
    private String category;
}
