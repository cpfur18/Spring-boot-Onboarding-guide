package com.asdf.minilog.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class FollowerRequestDto {
    @NonNull private Long followerId;
    @NonNull private Long followeeId;
}
