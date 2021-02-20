package com.taejin.study.jobs;

import com.taejin.study.domain.User;
import com.taejin.study.domain.UserStatus;
import com.taejin.study.jobs.readers.QueueItemReader;
import com.taejin.study.repository.UserRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class InactiveUserJobConfig {

    private final UserRepository userRepository;

    public InactiveUserJobConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
    *
    * SpringApplication 클래스 위에 @EnableBatchProcessing 어노테이션을 통해 기본 설정 JobBuilderFactory/StepBuilderFactory 주입 받는다.
    *
    * */
    @Bean
    public Job inactiveUserJob(JobBuilderFactory jobBuilderFactory, Step inactiveJobStep) {
        return jobBuilderFactory.get("inactiveUserJob")
                .preventRestart()
                .start(inactiveJobStep)
                .build();
    }

    @Bean
    public Step inactiveJobStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("inactiveUserStep")
                .<User, User> chunk(5)
                .reader(inactiveUserReader())
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();
    }

//    @Bean
//    @StepScope
//    public QueueItemReader<User> inactiveUserReader() {
//        List<User> oldUsers = userRepository.findByUpdatedAtBeforeAndStatusEquals(LocalDate.now().minusYears(1), UserStatus.ACTIVE);
//        return new QueueItemReader<>(oldUsers);
//    }

    @Bean
    @StepScope
    public ListItemReader<User> inactiveUserReader() {
        List<User> oldUsers = userRepository.findByUpdatedAtBeforeAndStatusEquals(LocalDate.now().minusYears(1L), UserStatus.ACTIVE);
        return new ListItemReader<>(oldUsers);
    }

    public ItemProcessor<User, User> inactiveUserProcessor() {
        return User::inActive;
    }

    public ItemWriter<User> inactiveUserWriter() {
        return userRepository::saveAll;
    }
}
