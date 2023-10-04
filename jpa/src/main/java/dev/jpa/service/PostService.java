package dev.jpa.service;

import dev.jpa.controller.PostDetailResponse;
import dev.jpa.domain.Post;
import dev.jpa.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Async
    public PostDetailResponse findPost(Long postId) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(3);

        for (int i = 0; i < 3; i++) {
            taskExecutor.submit(() -> {
                try {
                    postRepository.findById(postId);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("예외 발생"));
        countDownLatch.await();
        return PostDetailResponse.from(post);
    }

}
