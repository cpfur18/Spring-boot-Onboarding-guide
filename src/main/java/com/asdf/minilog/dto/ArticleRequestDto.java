package com.asdf.minilog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

// @Data
@Getter
@Builder
public class ArticleRequestDto {
    @NotNull private String content;
    @NotNull private Long authorId;
}
