package com.cia103g5.article.repository;

import com.cia103g5.article.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cia103g5.article.model.PostCategory;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
//	// JpaRepository 已經提供基本的資料庫操作方法，不需要額外實作
	 List<Post> findByPostCategory(PostCategory postCategory);
	 List<Post> findByPostCategory_CategoryName(String categoryName);
	 List<Post> findByPostCategoryOrderByPostTimeDesc(PostCategory postCategory);

	 
}
