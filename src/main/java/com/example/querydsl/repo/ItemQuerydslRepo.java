package com.example.querydsl.repo;


import com.example.querydsl.dto.ItemSearchParams;
import com.example.querydsl.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemQuerydslRepo {
    List<Item> searchDynamic(ItemSearchParams searchParams);

    // Querydsl에서 Pageable 사용해보기
    Page<Item> searchDynamic(ItemSearchParams searchParams, Pageable pageable);
}
