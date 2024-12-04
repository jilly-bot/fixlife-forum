package com.cia103g5.article.controller;

import com.cia103g5.article.model.Post;
import com.cia103g5.article.model.PostCategory;
import com.cia103g5.article.model.PostPic;
import com.cia103g5.article.repository.PostRepository;
import com.cia103g5.article.service.FavPostService;
import com.cia103g5.article.service.PostService;
import com.cia103g5.article.repository.PostPicRepository;
import com.cia103g5.article.repository.PostCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.jsoup.Jsoup;

@RestController
@RequestMapping("/api") // 設定 API 路徑前綴為 /api
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostPicRepository postPicRepository;

    @Autowired
    private PostCategoryRepository postCategoryRepository;

    @Autowired
    private FavPostService favPostService;

    @Autowired
    private PostService postService;

    // 發表新文章
    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        try {
            // 預設 memId 和 userType
            post.setMemId(6);
            post.setUserType((byte) 1);

            // 檢查分類是否存在或創建新分類
            if (post.getPostCategory() != null && post.getPostCategory().getCategoryName() != null) {
                PostCategory category = postCategoryRepository
                        .findByCategoryName(post.getPostCategory().getCategoryName())
                        .orElseGet(() -> {
                            PostCategory newCategory = new PostCategory();
                            newCategory.setCategoryName(post.getPostCategory().getCategoryName());
                            return postCategoryRepository.save(newCategory);
                        });
                post.setPostCategory(category);
            }

            // 清理 HTML 標籤並限制字數
            String cleanContent = post.getContent() != null ? Jsoup.parse(post.getContent()).text() : "";
            if (cleanContent.length() > 30) {
                cleanContent = cleanContent.substring(0, 30) + "...";
            }
            post.setContent(cleanContent);

            // 設定發文時間
            post.setPostTime(LocalDateTime.now());

            // 保存文章
            Post savedPost = postRepository.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

 // 上傳與文章相關的圖片
    @PostMapping("/posts/{postNo}/uploadPic")
    public ResponseEntity<String> uploadPic(@PathVariable("postNo") int postNo,
                                            @RequestParam("pic") MultipartFile file) {
        try {
            // 查詢文章是否存在
            Optional<Post> postOptional = postRepository.findById(postNo);
            if (!postOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("文章不存在，無法上傳圖片。");
            }

            // 確認檔案是否為空
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("請提供有效的圖片檔案。");
            }

            // 檢查圖片大小 (以 byte 為單位)
            long maxSizeInBytes = 1024 * 1024 * 16; // 16 MB，適合 longblob
            if (file.getSize() > maxSizeInBytes) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("圖片大小超出限制，請上傳小於 16 MB 的圖片。");
            }

            // 創建並保存圖片
            PostPic postPic = new PostPic();
            postPic.setPostNo(postNo);
            postPic.setPic(file.getBytes());
            postPicRepository.save(postPic);

            return ResponseEntity.ok("圖片上傳成功！");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("圖片處理失敗！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("圖片上傳失敗！");
        }
    }


    // 根據文章編號查詢文章資訊
    @GetMapping("/posts/{postNo}")
    public ResponseEntity<Post> getPostById(@PathVariable("postNo") int postNo) {
        Optional<Post> post = postRepository.findById(postNo);
        if (post.isPresent()) {
            return ResponseEntity.ok(post.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 更新文章資訊
    @PutMapping("/posts/{postNo}")
    public ResponseEntity<String> updatePost(@PathVariable("postNo") int postNo, 
                                             @RequestBody Post updatedPost) {
        try {
            Optional<Post> existingPost = postRepository.findById(postNo);
            if (existingPost.isPresent()) {
                Post post = existingPost.get();
                post.setTitle(updatedPost.getTitle());

                // 清理內容並限制字數
                String cleanContent = updatedPost.getContent() != null ? Jsoup.parse(updatedPost.getContent()).text() : "";
                if (cleanContent.length() > 100) {
                    cleanContent = cleanContent.substring(0, 100) + "...";
                }
                post.setContent(cleanContent);

                post.setAuthorName(updatedPost.getAuthorName());
                post.setModTime(LocalDateTime.now());

                // 更新分類
                if (updatedPost.getPostCategory() != null && updatedPost.getPostCategory().getCategoryName() != null) {
                    PostCategory category = postCategoryRepository
                            .findByCategoryName(updatedPost.getPostCategory().getCategoryName())
                            .orElseGet(() -> {
                                PostCategory newCategory = new PostCategory();
                                newCategory.setCategoryName(updatedPost.getPostCategory().getCategoryName());
                                return postCategoryRepository.save(newCategory);
                            });
                    post.setPostCategory(category);
                }

                postRepository.save(post);
                return ResponseEntity.ok("文章更新成功！");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到此文章，無法更新！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新文章失敗！");
        }
    }

    // 刪除指定文章
    @DeleteMapping("/posts/{postNo}")
    public ResponseEntity<String> deletePost(@PathVariable("postNo") int postNo) {
        try {
            Optional<Post> existingPost = postRepository.findById(postNo);
            if (existingPost.isPresent()) {
                postRepository.deleteById(postNo);
                return ResponseEntity.ok("文章已刪除！");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到此文章，無法刪除！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除文章失敗！");
        }
    }

    // 互動功能
    @PostMapping("/favorite/{postNo}")
    public ResponseEntity<Void> toggleFavorite(@PathVariable int postNo) {
        favPostService.toggleFavorite(6, postNo); // 預設 memId:6
        return ResponseEntity.ok().build();
    }

    @PostMapping("/like/{postNo}")
    public ResponseEntity<Void> toggleLike(@PathVariable int postNo) {
        postService.toggleLike(6, postNo); // 預設 memId:6
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dislike/{postNo}")
    public ResponseEntity<Void> toggleDislike(@PathVariable int postNo) {
        postService.toggleDislike(6, postNo); // 預設 memId:6
        return ResponseEntity.ok().build();
    }
}
