package com.asdf.todo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.asdf.todo.dto.TodoRequestDto;
import com.asdf.todo.dto.TodoResponseDto;
import com.asdf.todo.entity.Todo;
import com.asdf.todo.service.TodoService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired private MockMvc mockMvc; //
    @MockitoBean private TodoService todoService; // 테스트용 가짜 객체, 컨트롤러에선 굳이 DB 테스트 필요성 없음

    @Test
    void testGetTodoById() throws Exception {
        TodoResponseDto todo = new TodoResponseDto(1L, "Test Todo", "Description", false);

        given(todoService.findById(1L)).willReturn(todo);

        mockMvc.perform(get("/api/todos/v2/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Todo"));
    }

    @Test
    void testGetAllTodos() throws Exception {
        given(todoService.findAll()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/todos/v2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        given(todoService.findAll())
                .willReturn(
                        Collections.singletonList(
                                new TodoResponseDto(1L, "Test Todo", "Description",
                                        false)));

        mockMvc.perform(get("/api/todos/v2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Todo"));
    }

    @Test
    void testCreateTodo() throws Exception {
        TodoResponseDto todo = new TodoResponseDto(1L, "Test Todo", "Description",
                false);

        given(todoService.save(any(TodoRequestDto.class))).willReturn(todo);

        mockMvc.perform(
                        post("/api/todos/v2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"title\": \"New Todo\", \"decription\":"
                                        + "\"Description\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Todo"));
    }

    @Test
    void testUpdateTodo() throws Exception {
        TodoResponseDto existingTodo =
                new TodoResponseDto(1L, "Existing Todo", "Description", false);
        TodoResponseDto updatedTodo = new TodoResponseDto(1L, "Updated Todo", "Updated Description",
                true);

        given(todoService.findById(1L)).willReturn(existingTodo);
        given(todoService.update(anyLong(), any(TodoRequestDto.class))).willReturn(updatedTodo);

        mockMvc.perform(
                        put("/api/todos/v2/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"title\": \"Updated Todo\", \"description\": \"Updated Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Todo"));
    }

    @Test
    void testDeleteTodo() throws Exception {
        TodoResponseDto todo = new TodoResponseDto(1L, "Test Todo", "Description", false);

        given(todoService.findById(1L)).willReturn(todo);

        mockMvc.perform(delete("/api/todos/v2/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
