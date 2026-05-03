package com.asdf.minilog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    @NotNull private String username;
    @NotNull private String password;
}
