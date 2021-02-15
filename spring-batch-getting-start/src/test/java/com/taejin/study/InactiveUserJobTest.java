package com.taejin.study;

import com.taejin.study.domain.UserStatus;
import com.taejin.study.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class InactiveUserJobTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private UserRepository userRepository;

    @Test
    void 휴면회원_전환테스트() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(userRepository.findByUpdatedAtBeforeAndStatusEquals(LocalDate.now().minusYears(1), UserStatus.ACTIVE).size()).isEqualTo(0);
    }
}
