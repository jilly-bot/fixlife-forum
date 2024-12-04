package com.cia103g5.article.service;

import com.cia103g5.article.model.Post;
import com.cia103g5.article.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // 更新按讚數量
    public void updateLikes(int postNo, boolean isLike) {
        Post post = postRepository.findById(postNo).orElseThrow(() -> new RuntimeException("Post not found"));
        int currentLikes = post.getTotalLikes();
        post.setTotalLikes(isLike ? currentLikes + 1 : currentLikes - 1);
        postRepository.save(post);
    }

    // 更新倒讚數量
    public void updateDislikes(int postNo, boolean isDislike) {
        Post post = postRepository.findById(postNo).orElseThrow(() -> new RuntimeException("Post not found"));
        int currentDislikes = post.getTotalDislikes();
        post.setTotalDislikes(isDislike ? currentDislikes + 1 : currentDislikes - 1);
        postRepository.save(post);
    }
    public void toggleLike(int memId, int postNo) {
        Optional<Post> optionalPost = postRepository.findById(postNo);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            // 如果用戶已經按過讚，可以用另一個表來記錄該用戶的行為以判斷
            // 如果沒記錄，則添加讚
            if (post.getTotalLikes() > 0) {
            	post.setTotalLikes(0);
            } else {
            	post.setTotalLikes(1);
            	post.setTotalDislikes(0);
            }
            //post.setTotalLikes(post.getTotalLikes() + 1);
            postRepository.save(post);
        } else {
            throw new RuntimeException("找不到指定的文章，PostNo: " + postNo);
        }
    }

    public void toggleDislike(int memId, int postNo) {
        Optional<Post> optionalPost = postRepository.findById(postNo);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            // 如果用戶已經按過倒讚，可以用另一個表來記錄該用戶的行為以判斷
            // 如果沒記錄，則添加倒讚
            if (post.getTotalDislikes() > 0) {
            	post.setTotalDislikes(0);
            } else {
            	post.setTotalDislikes(1);
            	post.setTotalLikes(0);
            }
            //post.setTotalDislikes(post.getTotalDislikes() + 1);
            postRepository.save(post);
        } else {
            throw new RuntimeException("找不到指定的文章，PostNo: " + postNo);
        }
    }
}
