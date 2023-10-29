package me.nelonn.quillspace.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.nelonn.quillspace.config.ArticlesProperties;
import me.nelonn.quillspace.dto.Response;
import me.nelonn.quillspace.dto.RestError;
import me.nelonn.quillspace.model.Article;
import me.nelonn.quillspace.model.User;
import me.nelonn.quillspace.model.Views;
import me.nelonn.quillspace.repository.ArticleRepository;
import me.nelonn.quillspace.repository.UserRepository;
import me.nelonn.quillspace.security.SessionAuthentication;
import me.nelonn.quillspace.util.ErrorEntity;
import me.nelonn.quillspace.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticlesProperties articlesProperties;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Autowired
    public ArticleController(ArticlesProperties articlesProperties,
                             ArticleRepository articleRepository,
                             UserRepository userRepository) {
        this.articlesProperties = articlesProperties;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArticlePayload {
        private String title;
        private String summary;
        private String content;
    }

    @PostMapping
    @JsonView(Views.Public.class)
    public ResponseEntity<Response> createArticle(@RequestBody ArticlePayload articlePayload) {
        if (articlePayload.title == null) {
            return new ErrorEntity(RestError.TITLE_IS_EMPTY);
        }
        if (articlePayload.title.length() > articlesProperties.maxTitleLength()) {
            return new ErrorEntity(RestError.TITLE_TOO_LONG);
        }
        if (articlePayload.summary != null && articlePayload.summary.length() > articlesProperties.maxSummaryLength()) {
            return new ErrorEntity(RestError.SUMMARY_TOO_LONG);
        }
        if (articlePayload.content == null) {
            return new ErrorEntity(RestError.CONTENT_IS_EMPTY);
        }
        if (articlePayload.content.length() > articlesProperties.maxContentLength()) {
            return new ErrorEntity(RestError.CONTENT_TOO_LONG);
        }
        SessionAuthentication authentication = (SessionAuthentication) SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userRepository.findById(authentication.getSession().getUserId());
        if (userOptional.isEmpty()) {
            return new ErrorEntity(RestError.INTERNAL_SERVER_ERROR);
        }
        Article article = Article.builder()
                .title(articlePayload.getTitle())
                .summary(articlePayload.getSummary())
                .content(articlePayload.getContent())
                .author(userOptional.get()).build();
        article = articleRepository.save(article);
        return new ResultEntity<>(article);
    }

    @GetMapping("/{id}")
    @JsonView(Views.Public.class)
    public ResponseEntity<Response> getArticleById(@PathVariable("id") String id) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        return articleOptional.map(article -> (ResponseEntity<Response>) new ResultEntity<>(article))
                .orElseGet(() -> new ErrorEntity(RestError.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    @JsonView(Views.Public.class)
    public ResponseEntity<Response> updateArticleById(@PathVariable("id") String id, @RequestBody ArticlePayload articlePayload) {
        if (articlePayload.title != null && articlePayload.title.length() > articlesProperties.maxTitleLength()) {
            return new ErrorEntity(RestError.TITLE_TOO_LONG);
        }
        if (articlePayload.summary != null && articlePayload.summary.length() > articlesProperties.maxSummaryLength()) {
            return new ErrorEntity(RestError.SUMMARY_TOO_LONG);
        }
        if (articlePayload.content != null && articlePayload.content.length() > articlesProperties.maxContentLength()) {
            return new ErrorEntity(RestError.CONTENT_TOO_LONG);
        }
        Optional<Article> articleOptional = articleRepository.findById(id);
        if (articleOptional.isEmpty()) {
            return new ErrorEntity(RestError.NOT_FOUND);
        }
        Article existingArticle = articleOptional.get();
        SessionAuthentication authentication = (SessionAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if (!existingArticle.getAuthor().getId().equals(authentication.getSession().getUserId())) {
            return new ErrorEntity(RestError.NO_PERMISSION);
        }
        if (StringUtils.hasText(articlePayload.getTitle())) {
            existingArticle.setTitle(articlePayload.getTitle());
        }
        if (articlePayload.getSummary() != null) {
            existingArticle.setSummary(articlePayload.getSummary());
        }
        if (StringUtils.hasText(articlePayload.getContent())) {
            existingArticle.setContent(articlePayload.getContent());
        }
        Article updatedArticle = articleRepository.save(existingArticle);
        return new ResultEntity<>(updatedArticle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteArticleById(@PathVariable("id") String id) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        if (articleOptional.isEmpty()) {
            return new ErrorEntity(RestError.NOT_FOUND);
        }
        Article article = articleOptional.get();
        SessionAuthentication authentication = (SessionAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if (!article.getAuthor().getId().equals(authentication.getSession().getUserId())) {
            return new ErrorEntity(RestError.NO_PERMISSION);
        }
        articleRepository.deleteById(article.getId());
        return new ResultEntity<>(null);
    }

    @GetMapping
    @JsonView(Views.Public.class)
    public ResponseEntity<Response> searchArticles(@RequestParam(value = "title", required = false) String title,
                                                   @RequestParam(value = "summary", required = false) String summary) {
        if (!StringUtils.hasText(title) && !StringUtils.hasText(summary)) return new ResultEntity<>(Collections.emptyList());
        List<Article> articles = articleRepository.findByTitleContainingOrSummaryContaining(title, summary);
        return new ResultEntity<>(articles);
    }
}
