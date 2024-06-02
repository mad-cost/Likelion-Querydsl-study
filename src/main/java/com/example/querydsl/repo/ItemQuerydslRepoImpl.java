package com.example.querydsl.repo;

import com.example.querydsl.dto.ItemSearchParams;
import com.example.querydsl.entity.Item;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
    // TODO 동적 쿼리로 결과 반환하기
    return queryFactory.selectFrom(item).fetch();
  }

  // 페이지 처리만 해보기
  @Override
  public Page<Item> searchDynamic(ItemSearchParams searchParams, Pageable pageable) {
    // TODO 동적 쿼리로 결과 반환하기
    // Page를 만드는데 필요한 정보
    // 1. 실제 페이지에 담겨 있을 데이터 (offset, limit)
    List<Item> content = queryFactory
            .selectFrom(item)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize()) // 몇 번째 까지인지
            .fetch();
    // 2. Pageable에 대한 데이터 (몇 번째 페이지, 페이지 당 내용) -> 인자로 주어진다.
    // 3. 총 갯수 (총 페이지를 위해서 필요한 정보)
    Long count = queryFactory
            .select(item.count())
            .from(item)
            .fetchOne();
    // PageImpl로 반환
    return new PageImpl<>(content, pageable, count);
  }
}
