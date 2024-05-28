package com.example.querydsl;

import com.example.querydsl.entity.Item;
import com.example.querydsl.entity.QItem;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class QuerydslQTypeTests {
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
  public  void qType(){
    QItem qItem = new QItem("item");
    Item found = queryFactory
            .select(qItem)
            .from(qItem)
            .where(qItem.name.eq("itemA"))
            .fetchOne();

//    import static org.junit.jupiter.api.Assertions.assertEquals;
    assertEquals("itemA", found.getName());

    found = queryFactory
            // SELECT + FROM 절
            .selectFrom(qItem)
            .where(qItem.name.eq("itemB"))
            .fetchOne();
    assertEquals("itemB", found.getName());

    // item2 = 테스트의 결과를 확인해보면 QItem 생성자의 인자인 item2가 Alias(별명)로 동작한다
    QItem qItem2 = new QItem("item2");
    found = queryFactory
            .selectFrom(qItem2)
            .where(qItem2.name.eq("itemC"))
            .fetchOne();
    assertEquals("itemC", found.getName());

//    예외가 발생할 것이다
    assertThrows(Exception.class, () ->{
      queryFactory
//              SELECT item FROM item
              .selectFrom(qItem)
//              WHERE item2.name = "itemD" -> ??
              .where(qItem2.name.eq("itemD"))
              .fetchOne();
    });

  }

}


