package com.asdf.todo.service;

import com.asdf.todo.dto.TodoRequestDto;
import com.asdf.todo.dto.TodoResponseDto;
import com.asdf.todo.entity.Todo;
import com.asdf.todo.repository.TodoRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.asdf.todo.util.EntityDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Transactional(readOnly = true)
    public List<TodoResponseDto> findAll() {
        return todoRepository.findAll().stream()
                .map(EntityDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TodoResponseDto findById(Long id) {
        return todoRepository.findById(id).map(EntityDtoMapper::toDto).orElse(null);
    }

    @Transactional
    public TodoResponseDto save(TodoRequestDto todoRequestDto) {
        Todo todo = EntityDtoMapper.toEntity(todoRequestDto);
        Todo savedTodo = todoRepository.save(todo);
        return EntityDtoMapper.toDto(savedTodo);
    }

    @Transactional
    public TodoResponseDto update(Long id, TodoRequestDto todoRequestDto) {
        Todo todo = EntityDtoMapper.toEntity(todoRequestDto);
        todo.setId(id);
        Todo updatedTodo = todoRepository.save(todo);
        return EntityDtoMapper.toDto(updatedTodo);
    }

    @Transactional
    public void delete(Long id) { todoRepository.deleteById(id); }
}

// @Transactional는 ACID 보장하는 역할
// CUD는 @Transactional
// R은 @Transactional(readOnly = true)

// JPA는 DB에 처리한 결과가 담긴 객체를 다시 반환해준다.