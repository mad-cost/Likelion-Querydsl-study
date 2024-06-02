package com.example.querydsl;

import com.example.querydsl.dto.ItemDto;
import com.example.querydsl.dto.ItemDtoProj;
import com.example.querydsl.dto.QItemDtoProj;
import com.example.querydsl.entity.Item;
import com.example.querydsl.entity.Shop;
import com.example.querydsl.repo.ItemRepository;
import com.example.querydsl.repo.ShopRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
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
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class QuerydslProjTests {
  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private ShopRepository shopRepository;
  @Autowired
  private EntityManagerFactory managerFactory;
  @Autowired
  private JPAQueryFactory queryFactory;
  @Autowired
  private EntityManager entityManager;
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
  public void noProjection() {
    // 하나의 속성을 조회할때는, 해당 속성의 자료형으로 그대로 반환이 된다
    String name = queryFactory
            .select(item.name)
            .from(item)
            .where(item.id.eq(4L))
            .fetchOne();
    assertEquals("itemD", name);

    // 집계함수도 활용이 가능하다
    Long count = queryFactory
            .select(item.count())
            .from(item)
            .fetchOne();
    assertEquals(6L, count);

    // fetch는 리스트로
    List<String> names = queryFactory
            .select(item.name)
            .from(item)
            .fetch();
    for (String foundName : names){
      System.out.println(foundName);
    }

    // 단일 속성이 아니면 Tuple
      //projection : select 대상지정하는 일
      //projection이 두 개 이상이라면 튜플로 조회
    Tuple resultTuple = queryFactory
            .select(item.price, item.stock)
            .from(item)
            .where(item.name.eq("itemB"))
            .fetchOne();
    assertEquals(6000, resultTuple.get(item.price));
    assertEquals(30, resultTuple.get(item.stock));
    assertNull(resultTuple.get(item.name));

    List<Tuple> tuples = queryFactory
            .select(item.price, item.stock)
            .from(item)
            // 복수 이므로 .fetch();사용
            .fetch();
    for (Tuple tuple : tuples){
      System.out.printf(
              "%d (%d)%n",
              tuple.get(item.price),
              tuple.get(item.stock)
      );
    }
  }

  /* JPQL 에서 Dto사용
  SELECT new com.example.jpa.dto.ItemDto() ...
   */
  // Querydsl에서 Dto로 프로젝션 해보기
  @Test
  public void dtoProjection() {
    List<ItemDto> itemDtoList = null;

    itemDtoList = queryFactory
            // Projections.bean : Setter기반 Projection / ItemDto에 @Setter가 없을경우 null반환
            .select(Projections.bean(
                    ItemDto.class,
                    item.name,
                    item.price,
                    item.stock
            ))
            .from(item)
            .where(item.name.isNotNull())
            .fetch();
    itemDtoList.forEach(System.out::println);
    // id와 같은 값에는 setter가 필요하지 않을 수도 있다는 단점이 있음

    // Projections.fields: 속성 기반 Projection / ItemDto에 @Setter없이 사용 가능
    itemDtoList = queryFactory
            .select(Projections.fields(
                    ItemDto.class,
                    item.name,
                    item.price,
                    item.stock
            ))
            .from(item)
            .where(item.name.isNotNull())
            .fetch();
    itemDtoList.forEach(System.out::println);

    // Projections.constructor : 생성자 기반 Projection / @AllArgsConstructor 필요
    itemDtoList = queryFactory
            .select(Projections.constructor(
                    ItemDto.class,
                    item.name,
                    item.price,
                    item.stock
            ))
            .from(item)
            .where(
                    item.price.isNotNull(),
                    item.stock.isNotNull()
                    )
            .fetch();
    itemDtoList.forEach(System.out::println);

  }

  @Test
  public void queryProjection(){
    List<ItemDtoProj> itemDtoProjList = queryFactory
            // 만들어진 QDto의 생성자를 호출함으로써, 결과를 Dto로 받을 수 있다.
            .select(new QItemDtoProj(
                    // 타입 순서 주의
                    item.name,
                    item.price,
                    item.stock
            ))
            .from(item)
            .where(item.name.isNotNull())
            .fetch();
    itemDtoProjList.forEach(System.out::println);

  }
}

