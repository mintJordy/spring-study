package com.taejin.study.jobs;

import com.taejin.study.domain.User;
import com.taejin.study.domain.UserStatus;
import com.taejin.study.repository.UserRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class InactiveUserJobConfig {

    private static final int CHUNK_SIZE = 15;

    private final UserRepository userRepository;
    private final EntityManagerFactory entityManagerFactory;

    public InactiveUserJobConfig(UserRepository userRepository, EntityManagerFactory entityManagerFactory) {
        this.userRepository = userRepository;
        this.entityManagerFactory = entityManagerFactory;
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
                .<User, User> chunk(CHUNK_SIZE)
                .reader(inactiveUserReader())
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();
    }
    /*
    *  한번에 모든 데이터를 읽어 메모리에 로딩한다. 데이터 건수가 많아질 수록 메모리 관련 이슈 발생
    * */
    @Bean
    @StepScope
    public ListItemReader<User> inactiveUserReader() {
        List<User> oldUsers = userRepository.findByUpdatedAtBeforeAndStatusEquals(LocalDate.now().minusYears(1L), UserStatus.ACTIVE);
        return new ListItemReader<>(oldUsers);
    }

    /*
    *  한번에 설정한 Paging Size 만큼, 데이터를 읽어온다. 대신, Reader를 통해 데이터를 읽을 때는 raw sql문을 통해서 만 읽어 올 수 있다.
    * */
    @Bean(destroyMethod = "")
    @StepScope
    public JpaPagingItemReader<User> inactiveUserJpaReader() {
        JpaPagingItemReader<User> jpaPagingItemReader = new JpaPagingItemReader<>() {
            @Override
            public int getPage() {
                return 0;
            }
        };
        jpaPagingItemReader.setQueryString("select u from User as u where u.updatedAt < :updatedAt and u.status = :status");

        Map<String, Object> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        map.put("updatedAt", now.minusYears(1L));
        map.put("status", UserStatus.ACTIVE);
        jpaPagingItemReader.setParameterValues(map);
        jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
        jpaPagingItemReader.setPageSize(CHUNK_SIZE);

//        JpaPagingItemReader<User> userReader = new JpaPagingItemReaderBuilder<User>()
//                .name("userReader")
//                .entityManagerFactory(entityManagerFactory)
//                .queryString("select u from User as u where u.updatedAt < :updatedAt and u.status = :status")
//                .parameterValues(map)
//                .pageSize(CHUNK_SIZE)
//                .build();

        return jpaPagingItemReader;
    }

    public ItemProcessor<User, User> inactiveUserProcessor() {
        return User::inActive;
    }

    public ItemWriter<User> inactiveUserWriter() {
        return userRepository::saveAll;
    }
}
