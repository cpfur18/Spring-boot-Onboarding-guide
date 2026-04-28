package com.asdf.todo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false, name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(nullable = false, name = "completed")
    private boolean completed;

    @Column(
            nullable = false,
            name = "create_at",
            insertable = false, // INSERT 시 제외
            updatable = false, // UPDATE 시 제외
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
//    @CreationTimestamp 위 코드 실전 압축
//    @Column(updatable = false)
    private LocalDateTime createdAt;

}
