package com.vhais.blog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

// can't use @Data as it generates hashCode using fields. This is problematic with usage of Set when fetching from database
@Entity
@Table
@Getter
@Setter
@ToString
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @NotNull
    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts;
}
