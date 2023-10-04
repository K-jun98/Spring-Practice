package dev.jpa.controller;

import dev.jpa.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("api/posts/{postId}")
    public PostDetailResponse findPost(@PathVariable Long postId) throws InterruptedException {
        return postService.findPost(postId);
    }

}
