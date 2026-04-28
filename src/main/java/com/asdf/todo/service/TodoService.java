package com.asdf.todo.service;

import com.asdf.todo.entity.Todo;
import com.asdf.todo.repository.TodoMemoryRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TodoService {
    private final TodoMemoryRepository todoRepository;

    @Autowired
    public TodoService(TodoMemoryRepository todoMemoryRepository) {
        this.todoRepository = todoMemoryRepository;
    }

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    public Todo findById(Long id) {
        return todoRepository.findById(id);
    }

    public Todo save(Todo todo) {
        return todoRepository.save(todo);
    }

    public Todo update(Long id, Todo todo) {
        todo.setId(id);
        return todoRepository.save(todo);
    }

    public void delete(Long id) {
        todoRepository.deleteById(id);
    }
}
