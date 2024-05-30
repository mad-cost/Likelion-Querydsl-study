package com.example.querydsl;

import com.example.querydsl.entity.Item;
import com.example.querydsl.entity.Shop;
import com.example.querydsl.repo.ItemRepository;
import com.example.querydsl.repo.ShopRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.example.querydsl.entity.QItem.item;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class QuerydslQueryTypeTests {
  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private ShopRepository shopRepository;
  @Autowired
  private JPAQueryFactory queryFactory;

  // @BeforeEach: 각 테스트 전에 실행할 코드를 작성하는 영역
  @BeforeEach
  public void beforeEach() {
    Shop shopA = shopRepository.save(Shop.builder()
            .name("shopA")
            .description("shop A description")
            .build());
    Shop shopB = shopRepository.save(Shop.builder()
            .name("shopB")
            .description("shop B description")
            .build());

    itemRepository.saveAll(List.of(
            Item.builder()
                    .shop(shopA)
                    .name("itemA")
                    .price(5000)
                    .stock(20)
                    .build(),
            Item.builder()
                    .shop(shopA)
                    .name("itemB")
                    .price(6000)
                    .stock(30)
                    .build(),
            Item.builder()
                    .shop(shopB)
                    .name("itemC")
                    .price(8000)
                    .stock(40)
                    .build(),
            Item.builder()
                    .shop(shopB)
                    .name("itemD")
                    .price(10000)
                    .stock(50)
                    .build(),
            Item.builder()
                    .name("itemE")
                    .price(11000)
                    .stock(10)
                    .build(),
            Item.builder()
                    .price(10500)
                    .stock(25)
                    .build()
    ));
  }

  @Test
  public void fetch() {
    // fetch() : 전체 조회
    List<Item> foundList = queryFactory
            // item = import static com.example.querydsl.entity.QItem.item;
            // SELECT FROM 절
            .selectFrom(item)
            // 결과를 리스트 형태로 조회
            .fetch();
    assertEquals(6,foundList.size());

    // fetchOne() : 하나만 조회
    Item found = queryFactory
            .selectFrom(item)
            // item의 id가 1인 값을 조회
            .where(item.id.eq(1L))
            // 하나만 조회
            .fetchOne();
    assertEquals(1L, found.getId());

    found = queryFactory
            .selectFrom(item)
            .where(item.id.eq(0L))
            // item의 id에 0이 없을 경우 null
            .fetchOne();
    assertNull(found);

    //  예외가 발생할 것이다
    assertThrows(Exception.class, () -> {
      queryFactory.selectFrom(item)
              // 2개 이상일 경우 예외 발생
              .fetchOne();
    });

    // offset / limit
    foundList = queryFactory
            .selectFrom(item)
            // 3번까지 건너 뛰고, 4번부터 시작한다
            .offset(3)
            .limit(2)
            .fetch();
    for (Item find : foundList){
      System.out.println(find.getId()); //4 5 출력
      System.out.println(find.getName());

    }

  }

  @Test
  // 정렬 사용
  public void sort() {
    itemRepository.saveAll(List.of(
            Item.builder()
                    .name("itemF")
                    .price(6000)
                    .stock(40)
                    .build(),
            Item.builder()
                    .price(6000)
                    .stock(40)
                    .build())
    );
//  6000원 비교해보기
    List<Item> foundList = queryFactory
            // SELECT i FORM item i
            .selectFrom(item)
            // ORDER BY i.price ASC
            .orderBy(
//                    가격 기준 오름차순정렬, 같은 가격의 경우 stock 역순으로 정렬, 가격과 stock이 같을 경우 null을 먼저 정렬
                    // 오름차순 정렬
                    item.price.asc(),
                    // stock기준 역순으로 정렬
                    item.stock.desc(),
                    // null을 먼저 출력
                    item.name.asc().nullsFirst()
//                    item.name.asc().nullsLast()

            )
            .fetch();

    for (Item found : foundList){
      System.out.printf("%s: %d (%d)%n", found.getName(), found.getPrice(), found.getStock());
    }

  }

}



