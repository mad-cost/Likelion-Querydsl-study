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
    // entityManager 받아서 jpa를 사용해서 데이터 베이스를 조회하는 Querydsl의 모듈
    // jpa를 사용할 때는 영속성 컨텍스트와 소통하기 위해 entityManager가 필요하고, entityManager를 jpaQueryFactory객체에 넣어준다
    public JPAQueryFactory jpaQueryFactory(
            EntityManager entityManager
    ){
        return new JPAQueryFactory(entityManager);
    }
}
