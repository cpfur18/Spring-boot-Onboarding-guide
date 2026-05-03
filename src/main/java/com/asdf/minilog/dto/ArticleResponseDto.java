package com.asdf.minilog.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class ArticleResponseDto {
    @NonNull private Long articleId;
    @NonNull private String content;
    @NonNull private Long authorId;
    @NonNull private String authorName;
    @NonNull private LocalDateTime createdAt;
}
