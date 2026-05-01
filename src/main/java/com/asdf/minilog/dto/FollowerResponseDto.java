package com.asdf.minilog.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;

@Getter
@Builder
public class FollowerResponseDto {
    @NonNull private Long followerId;
    @NonNull private Long followeeId;
}
