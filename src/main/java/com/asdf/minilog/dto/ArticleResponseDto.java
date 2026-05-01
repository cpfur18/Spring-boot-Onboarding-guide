package com.asdf.minilog.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;

@Getter
@Builder
public class ArticleResponseDto {
    @NonNull private Long articleId;
    @NonNull private String content;
    @NonNull private Long authorId;
    @NonNull private String authorName;
    @NonNull private LocalDateTime createdAt;
}
