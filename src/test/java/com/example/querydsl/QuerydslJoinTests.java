package com.example.querydsl;

import com.example.querydsl.entity.Item;
import com.example.querydsl.entity.Shop;
import com.example.querydsl.repo.ItemRepository;
import com.example.querydsl.repo.ShopRepository;
import com.querydsl.core.Tuple;
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

import java.time.LocalDateTime;
import java.util.List;

import static com.example.querydsl.entity.QItem.item;
import static com.example.querydsl.entity.QShop.shop;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class QuerydslJoinTests {
  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private ShopRepository shopRepository;
  @Autowired
  private EntityManagerFactory managerFactory;
  @Autowired
  private JPAQueryFactory queryFactory;
  private PersistenceUnitUtil unitUtil;

  // @BeforeEach: 각 테스트 전에 실행할 코드를 작성하는 영역
  @BeforeEach
  public void beforeEach() {
    unitUtil = managerFactory.getPersistenceUnitUtil();
//    Item temp = Item.builder().build();
//    unitUtil.isLoaded(temp.getShop());

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

  @Test
  public void regularJoins(){
    List<Item> foundList = queryFactory
            .selectFrom(item)
            // INNER JOIN (교집합)
//            .join(item.shop)
            // LEFT JOIN (합집합)
//             .leftJoin(item.shop)
            // RIGHT JOIN
//            .rightJoin(item.shop)
            .fetch();

    for (Item found: foundList){
      System.out.println(found);
    }

  }

  @Autowired
  private EntityManager entityManager;
  @Test
  public void fetchJoin(){
    // 영속성 컨텍스트 초기화
    entityManager.flush();
    entityManager.clear();

    Item found = queryFactory
            .selectFrom(item)
            // 그냥 join은 연관 데이터를 불러오지는 않는다
            .join(item.shop)
            .where(item.name.eq("itemA"))
            .fetchOne();
    // 검색한 데이터의 Shop 데이터는 가져와 지지 않은 상태
    assertFalse(unitUtil.isLoaded(found.getShop()));

    found = queryFactory
            .selectFrom(item)
            .join(item.shop)
            // Fetch Join으로 변경
            // fetch join은 연관관계의 데이터를 같이 가져온다
            .fetchJoin()
            .where(item.name.eq("itemB"))
            .fetchOne();
    // 검색한 데이터의 Shop 데이터는 가져와진 상태
    assertTrue(unitUtil.isLoaded(found.getShop()));
  }

  @Test
//  집계함수
  // 집계함수란? = 여러 행으로부터 하나의 결과값을 반환하는 함수이다
  // SELECT COUNT(item), MAX(item.price) FROM Item item;
  public void aggregate(){
    // Tuple은 조회했던 QType의 속성 및 집계를 기준으로 데이터 회수 가능
      // projection : select 대상지정하는 일
      // projection이 두 개 이상이라면 튜플로 조회
    Tuple result = queryFactory
            // 집계하고 싶은 속성의 집계함수 메서드를 호출하여 select에 추가
            // item.(속성).(집계함수)()
            .select(
                    item.count(),
                    item.price.avg(),
                    item.price.max(),
                    item.stock.sum()
            )
            .from(item)
            .fetchOne();
    assertEquals(6, result.get(item.count()));
    assertEquals(175, result.get(item.stock.sum()));
  }

}





