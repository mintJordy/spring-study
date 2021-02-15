package com.taejin.study;

import com.taejin.study.domain.User;
import com.taejin.study.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class InitialDataRunner implements ApplicationRunner {

    private final UserRepository userRepository;

    public InitialDataRunner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<User> users = Arrays.asList(
                createUser("test@test.com", "최철"),
                createUser("test2@test.com", "김철"),
                createUser("test3@test.com", "강철"),
                createOldUser("test4@test.com", "약철"),
                createOldUser("test5@test.com", "약금"),
                createOldUser("test6@test.com", "강금"),
                createOldUser("test7@test.com", "강약"),
                createOldUser("test8@test.com", "강약약"),
                createOldUser("test9@test.com", "중약약"),
                createOldUser("test10@test.com", "중약약2"),
                createOldUser("test11@test.com", "중약약3")
        );

        userRepository.saveAll(users);
    }

    private User createUser(String email, String name) {
        return User.builder()
                .email(email)
                .name(name)
                .password("1234")
                .createdAt(LocalDate.now().minusYears(3))
                .updatedAt(LocalDate.now().minusMonths(1))
                .build();
    }

    private User createOldUser(String email, String name) {
        return User.builder()
                .email(email)
                .name(name)
                .password("1234")
                .createdAt(LocalDate.now().minusYears(3))
                .updatedAt(LocalDate.now().minusYears(2))
                .build();
    }
}
