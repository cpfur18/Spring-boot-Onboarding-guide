package com.asdf.minilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

// @Data
@Getter
@Builder
public class ArticleRequestDto {
    @NotNull private String content;

    @Deprecated(since = "2.0", forRemoval = true)
    @Schema(
            description = "작성자 ID (이 필드는 더 이상 사용되지 않습니다.)",
            example = "0", // Swagger UI 샘플 데이터 값
            required = true, // 필수 입력 여부
            deprecated = true) // API 사용자 경고
    private Long authorId;
}
