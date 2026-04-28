package com.asdf.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponseDto {

    @NonNull private Long id;
    @NonNull private String title;
    @NonNull private String description;
    private boolean completed = false;

}
