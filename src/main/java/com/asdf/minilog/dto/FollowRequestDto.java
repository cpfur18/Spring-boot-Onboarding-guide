package com.asdf.minilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowRequestDto {

    @Deprecated(since = "2.0", forRemoval = true)
    @Schema(
            description = "팔로워 ID (이 필드는 더 이상 사용되지 않습니다.)",
            example = "0",
            readOnly = true,
            deprecated = true
    )
    private Long followerId;
    @NotNull private Long followeeId;
}
