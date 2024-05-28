package com.example.querydsl.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.prefs.BackingStoreException;

@Configuration
@EnableJpaAuditing
//@RequiredArgsConstructor
public class JpaConfig {
//    private final EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory(
            EntityManager entityManager
    ){
        return new JPAQueryFactory(entityManager);
    }
}
