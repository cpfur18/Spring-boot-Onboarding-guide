package com.asdf.todo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class TodoRequestDto {

    @NonNull private String title;
    @NonNull private String description;
    private boolean completed = false;

    public TodoRequestDto(@NonNull String title, @NonNull String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public TodoRequestDto(@NonNull String title, @NonNull String description) {
        this(title, description, false);
    }
}
