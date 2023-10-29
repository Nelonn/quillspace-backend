package me.nelonn.quillspace.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "articles")
public record ArticlesProperties(int maxTitleLength, int maxSummaryLength, int maxContentLength) {
}
