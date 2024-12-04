package com.cia103g5.article.repository;

import com.cia103g5.article.model.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Integer> {
    Optional<PostCategory> findByCategoryName(String categoryName);
}
