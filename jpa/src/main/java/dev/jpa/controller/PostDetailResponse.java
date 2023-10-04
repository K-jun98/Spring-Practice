package dev.jpa.controller;

import dev.jpa.domain.Post;

public record PostDetailResponse(String title, String content) {
    public static PostDetailResponse from(Post post) {
        return new PostDetailResponse(post.getTitle(), post.getContent());
    }
}
