package com.asdf.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.asdf.todo.dto.TodoRequestDto;
import com.asdf.todo.dto.TodoResponseDto;
import com.asdf.todo.entity.Todo;
import com.asdf.todo.repository.TodoRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers // Docker 컨테이너를 자동으로 띄어서 테스트 환경 구축, 테스트마다 깨끗한 환경 생성
//@ExtendWith(SpringExtension.class) // @ExtendWith : Junit5 확장 기능 붙이는 어노테이션, SpringExtension : 스프링테스트 기능 제공
@SpringBootTest // @ExtendWith(SpringExtension.class)는 만약 SpringBoot 없이 테스트 하고 싶을 시 사용
public class TodoServiceTest {
    @Container // 테스트 실행 시 자동으로 시작/종료 되는 컨테이너
    public static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15") // 실제 도커 이미지가 존재해야함
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource // Testcontainers에서 생성된 DB 접속 정보를 Spring 설정에 동적 수정 : DB, Redis, Kafka 등에 사용
    static void configureProperties(DynamicPropertyRegistry registry) {
        // 컨테이너에서 실행된 DB의 url, username, password를 Spring 설정에 넣음
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired private TodoService todoService;
    @Autowired private TodoRepository todoRepository;

    private Long todo1Id;
    private Long todo2Id;


    @BeforeEach
    void setUp() {
        todoRepository.deleteAll(); // 테스트 시 DB 초기화

        // insert 한 뒤 결과를 todoId에 저장
        todo1Id = todoService.save(new TodoRequestDto("Test Todo 1", "Description 1")).getId();
        todo2Id = todoService.save(new TodoRequestDto("Test Todo 2", "Description 2")).getId();
    }

    @Test
    void testFindAll() {
        List<TodoResponseDto> todos = todoService.findAll();
        assertThat(todos).hasSize(2);
    }

    @Test
    void testSaveTodo() {
        TodoRequestDto todoRequestDto = new TodoRequestDto("New Todo", "New Description");
        todoService.save(todoRequestDto);
        assertThat(todoService.findAll()).hasSize(3);
    }

    @Test
    void testFindById() {
        TodoResponseDto todo = todoService.findById(todo1Id);
        assertThat(todo).isNotNull();
        assertThat(todo.getTitle()).isEqualTo("Test Todo 1");
    }

    @Test
    void testUpdateTodo() {
        TodoRequestDto updatedTodo =
                new TodoRequestDto("Updated Todo", "Updated Description", true);
        todoService.update(todo1Id, updatedTodo);
        TodoResponseDto todo = todoService.findById(todo1Id);
        assertThat(todo.getTitle()).isEqualTo("Updated Todo");
        assertThat(todo.getDescription()).isEqualTo("Updated Description");
        assertThat(todo.isCompleted()).isTrue();
    }

    @Test
    void testDeleteTodo() {
        todoService.delete(todo1Id);
        assertThat(todoService.findAll()).hasSize(1);
        assertThat(todoService.findById(todo1Id)).isNull();
    }
}
