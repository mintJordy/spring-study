package com.taejin.study.repository;

import com.taejin.study.domain.User;
import com.taejin.study.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUpdatedAtBeforeAndStatusEquals(LocalDate minusYears, UserStatus active);
}
