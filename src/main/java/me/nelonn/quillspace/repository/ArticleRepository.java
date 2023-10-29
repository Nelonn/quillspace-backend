package me.nelonn.quillspace.repository;

import me.nelonn.quillspace.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, String> {
    List<Article> findByTitleContaining(String title);

    List<Article> findBySummaryContaining(String summary);

    List<Article> findByTitleContainingOrSummaryContaining(String title, String summary);
}
