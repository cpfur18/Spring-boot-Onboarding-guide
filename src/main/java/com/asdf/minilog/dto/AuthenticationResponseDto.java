package com.asdf.minilog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponseDto {
    @NotNull private String jwt;
}
