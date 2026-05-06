package com.asdf.minilog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class) // 엔티티가 저장/수정 할 때 자동으로 날짜 등을 채워줌
public class User {
    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Id // PRIMARY KEY
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 값 자동 할당
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @CreatedDate // @EntityListeners(AuditingEntityListener.class) 있어야 동작
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false) // null 최대한 줄여 일관성 유지
    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;

    @OneToMany( // @OneToMany은 FK키를 반대쪽 테이블이 들고 있어서 구조 어색해서 잘 사용하지 않음
            mappedBy = "author", // 연관관계 주인 설
            cascade = CascadeType.ALL,
            orphanRemoval = true, // 관계 사라질 시 해당 Article 삭제
            fetch = FetchType.LAZY) // User조회 시 articles 안 가져옴, N + 1 문제 시 fetch join 또는 batch fetch
    private List<Article> articles;

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public void setPassword(String password) {
        this.password = passwordEncoder.encode(password);
    }

    public static class UserBuilder {
        private Long id;
        private String username;
        private String password;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<Article> articles;
        private static PasswordEncoder passwordEncoder = User.passwordEncoder;
        private Set<Role> roles;

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = passwordEncoder.encode(password);
            return this;
        }

        public UserBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserBuilder articles(List<Article> articles) {
            this.articles = articles;
            return this;
        }

        public UserBuilder roles(Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        public User build() {
            User user = new User();
            user.id = this.id;
            user.username = this.username;
            user.password = this.password;
            user.createdAt = this.createdAt;
            user.updatedAt = this.updatedAt;
            user.articles = this.articles;
            user.roles = this.roles;
            return user;
        }

    }
}
