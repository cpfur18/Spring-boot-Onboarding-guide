package com.asdf.todo.util;

import com.asdf.todo.dto.TodoRequestDto;
import com.asdf.todo.dto.TodoResponseDto;
import com.asdf.todo.entity.Todo;

public class EntityDtoMapper {

    public static Todo toEntity(TodoRequestDto dto) {
        return new Todo(
                null,
                dto.getTitle(),
                dto.getDescription(),
                dto.isCompleted(),
                null // 자동 생성 필드
        );
    }

    public static TodoResponseDto toDto(Todo entity) {
        return new TodoResponseDto(
                entity.getId(), entity.getTitle(), entity.getDescription(), entity.isCompleted());
    }

}
