package com.taejin.study.domain;

import lombok.Builder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String password;
    @Column
    private String email;
    @Column
    private UserStatus status = UserStatus.ACTIVE;
    @Column
    private LocalDate createdAt = LocalDate.now();
    @Column
    private LocalDate updatedAt = LocalDate.now();

    protected User() {
    }

    @Builder
    public User(String name, String password, String email, UserStatus status, LocalDate createdAt, LocalDate updatedAt) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User inActive() {
        this.status = UserStatus.INACTIVE;
        return this;
    }
}
