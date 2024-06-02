package com.example.querydsl.repo;

import com.example.querydsl.dto.ItemSearchParams;
import com.example.querydsl.entity.Item;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.querydsl.entity.QItem.item;

@RequiredArgsConstructor
// ItemQuerydslRepo의 구현체(Impl)
// 공식문서 참고 : https://docs.spring.io/spring-data/jpa/reference/repositories/custom-implementations.html
public class ItemQuerydslRepoImpl implements ItemQuerydslRepo {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<Item> searchDynamic(ItemSearchParams searchParams) {
    // import static com.example.querydsl.entity.QItem.item;
    return queryFactory.selectFrom(item).fetch();
  }
}
