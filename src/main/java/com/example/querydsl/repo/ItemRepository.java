package com.example.querydsl.repo;

import com.example.querydsl.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository
        extends JpaRepository<Item, Long>,
                // 다중상속
                ItemQuerydslRepo{ }
