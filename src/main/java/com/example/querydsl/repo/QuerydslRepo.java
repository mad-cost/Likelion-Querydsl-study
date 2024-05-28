package com.example.querydsl.repo;

import com.example.querydsl.entity.Item;
import com.example.querydsl.entity.QItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class QuerydslRepo {
  // Query를 만들기 위한 빌더 역할을 한다
  private final JPAQueryFactory queryFactory;
  private final ItemRepository itemRepository;
//  public QuerydslRepo(
//          EntityManager entityManager
//  ){
//      queryFactory = new JPAQueryFactory(entityManager);
//  }

  public void helloQuerydsl() {
    itemRepository.save(Item.builder()
            .name("new item")
            .price(1000)
            .stock(1000)
            .build());
      /*
      JPQL
      SELECT i
      FROM Item i
      // Item 엔티티의 이름이 특정 값과 일치하는 항목을 검색하는 역할
      WHERE i.name = :name
       */

    // ▲JPQL(주석) / ▼Querydsl
    
    // QuerydslQTypeTests에 TestCode 작성
    // QItem은 엔티티 그리고 엔티티가 가질 수 있는 속성을 나타낸다.
    QItem qItem = new QItem("item");
    List<Item> items = queryFactory
            // SELECT 절 추가
            .select(qItem)
            // FROM 절 추가
            .from(qItem)
            // WEHERE 절 추가
            .where(qItem.name.eq("new item"))
            // 결과 조회
            .fetch();


    for (Item item : items) {
      log.info("{}", item.getId());
      log.info("{} : {} ({})", item.getName(), item.getPrice(), item.getStock());
    }
  }
}
