package com.shop.controller;

import com.shop.constant.ItemSellStatus;
import com.shop.constant.OrderStatus;
import com.shop.constant.Role;
import com.shop.dto.ItemFormDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import com.shop.service.ItemService;
import com.shop.service.MemberService;
import com.shop.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderControllerTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    public Item saveItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    public Member saveMember(){
        Member member = new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);

    }

    @Test
    @DisplayName("주문 취소 테스트")
    public void cancelOrder() {
        Item item = saveItem(); // 테스트를 위해서 상품과 회원 데이터를 생성합니다. 생성한 상품의 재고는 100개입니다.
        Member member = saveMember(); // 테스트를 위해서 상품과 회원 데이터를 생성합니다. 생성한 상품의 재고는 100개입니다.

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());
        Long orderId = orderService.order(orderDto , member.getEmail()); // 테스트를 위해서 주문 데이터를 생성합니다 주문 개수는 총 10개입니다.

        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new); // 생성한 주문 엔티티를 조회합니다.
        orderService.cancelOrder(orderId); // 해당 주문을 취소합니다.

        assertEquals(OrderStatus.CANCEL , order.getOrderStatus()); // 주문의 상태가 취소 상태라면 테스트가 통과합니다.
        assertEquals(100 , item.getStockNumber()); // 취소 후 상품의 재고가 처음 재고 개수인 100개와 동일하다면 테스트가 통과합니다.
    }
}