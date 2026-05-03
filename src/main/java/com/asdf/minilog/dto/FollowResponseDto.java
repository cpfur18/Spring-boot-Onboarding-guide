package com.asdf.minilog.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class FollowResponseDto {
    @NonNull private Long followerId;
    @NonNull private Long followeeId;
}
