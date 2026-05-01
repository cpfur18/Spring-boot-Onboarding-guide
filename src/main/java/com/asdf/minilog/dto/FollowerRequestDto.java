package com.asdf.minilog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
public class FollowerRequestDto {
    @NotNull private Long followerId;
    @NotNull private Long followeeId;
}
