package com.example.querydsl;

import com.example.querydsl.dto.ItemDto;
import com.example.querydsl.dto.ItemDtoProj;
import com.example.querydsl.dto.QItemDtoProj;
import com.example.querydsl.entity.Item;
import com.example.querydsl.entity.Shop;
import com.example.querydsl.repo.ItemRepository;
import com.example.querydsl.repo.ShopRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.example.querydsl.entity.QItem.item;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
// 동적 쿼리 연습해보기
public class QuerydslDynamicQueryTests {
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
    Shop shopC = shopRepository.save(Shop.builder()
            .name("shopC")
            .description("shop C description")
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

  // 동적 쿼리 연습
  @Test
  public void dynamicQueryTester(){
    List<Item> results = null;
    String name = "itemA";
    Integer price = 5000;
    Integer stock = 20;

    results = booleanBuilder(name, price, stock);
    results.forEach(System.out::println);

    // 이름과 가격에 상관없이 재고가 20인 항목 목록.
    results = booleanBuilder(null, null, stock);
    results.forEach(System.out::println);

    // 가격과 재고에 상관없이 이름이 'itemA'인 항목 목록.
    results = booleanBuilder(name, null, null);
    results.forEach(System.out::println);

    // 이름과 재고에 상관없이 가격이 5000인 항목 목록.
    results = booleanBuilder(null, price, null);
    results.forEach(System.out::println);

    // 검색에서 가격으로 물품 찾기
    results = goeOrLoeOrBetween(6000, null); // 6000원 이상
    results.forEach(System.out::println);
    results = goeOrLoeOrBetween( null, 6000); // 6000원 이하
    results.forEach(System.out::println);
    results = goeOrLoeOrBetween( 5500, 7500);
    results.forEach(System.out::println);


  }

  // <= (less or equals), >= (greater or equals)
  public List<Item> goeOrLoeOrBetween(
          // 둘 다 있으면 사잇값
          // 하나만 있으면, floor는 최솟값, ceil은 최댓값
          Integer priceFloor, // 최솟값
          Integer priceCeil // 최댓값
  ){
    return queryFactory
            .selectFrom(item)
            .where(priceBetween(priceFloor, priceCeil))
            .fetch();
  }

  // priceLoe, priceGoe 사용해서 사잇값 구하기 (조건의 재활용)
  private BooleanExpression priceBetween(Integer floor, Integer ceil){
    if (floor == null && ceil == null) return null;
    if (floor == null) return priceLoe(ceil);
    if (ceil == null) return priceGoe(floor);
    return item.price.between(floor, ceil);
  }

  private BooleanExpression priceLoe(Integer price){
    return price != null ? item.price.loe(price) : null;
  }

  private BooleanExpression priceGoe(Integer price){
    return price != null ? item.price.goe(price) : null;
  }




  public List<Item> booleanExpression(
          String name,
          Integer price,
          Integer stock
  ){
    return queryFactory
            .selectFrom(item)
            .where(
                    // booleanBuilder와 결과가 똑같이 나오게끔 하고싶은데
                    // 단 인자가 null 이라면 들어가지 않게끔
                    // -> where 메서드는 null을 인자로 받으면 무시한다
                    item.name.eq(name),
                    null,null,null,null,null, // <- 문제가 되지 않는다 (무시한다)
                    item.price.eq(price),
                    item.stock.eq(stock)
            )
            .fetch();
  }


    // Test에서 사용할 메서드
  public List<Item>booleanBuilder(
          String name,
          Integer price,
          Integer stock
  ){
    // 1. BooleanBuilder: 여러 조건을 엮어서 하나의 조건으로 만들어진 booleanBuilder 사용법
    // item.name.isNotNull(): 생성자에 초기 조건 상정 가능
    // item.name이 null이 아닌 항목들을 포함하도록 설정
    BooleanBuilder booleanBuilder = new BooleanBuilder(item.name.isNotNull());
    // 동적 쿼리로 만들어주기
    if (name != null)
      // 여태까지 누적된 조건에 대하여, 주어진 조건을 AND로 엮는다.
      // (여태까지의 조건) AND i.name = name
      booleanBuilder.and(item.name.eq(name));
    if (price != null)
      // (여태까지의 조건) AND i.price = price
      booleanBuilder.and(item.price.eq(price));
    if (stock != null)
      // (여태까지의 조건) AND i.stock = stock
      booleanBuilder.and(item.stock.eq(stock));

    // booleanBuilder의 최종 조건: i.name = name AND i.price =price AND i.stock = stock
    return queryFactory
            .selectFrom(item)
            .where(booleanBuilder)
            .fetch();
  }

}
