package com.asdf.minilog.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@Builder
public class FollowerResponseDto {
    @NonNull private Long followerId;
    @NonNull private Long followeeId;
}
