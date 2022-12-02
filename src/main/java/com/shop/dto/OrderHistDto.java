package com.shop.dto;

import com.shop.constant.OrderStatus;
import com.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderHistDto {

    private Long orderId; // 주문 아이디

    private String orderDate; // 주문 날짜

    private OrderStatus orderStatus; // 주문 상태

    private List<OrderItemDto> orderItemDtoList = new ArrayList<>(); // 주문 상품 리스트

    // OrderHistDto 클래스의 생성자로 order 객체를 파라미터로 받아서 멤버 변수 값을 세팅합니다. 주문 날짜의 경우
    // 화면에 "yyyy-MM-dd HH:mm" 형태로 전달하기 위해서 포맷을 수정합니다.
    public OrderHistDto(Order order) {

        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }

    public void addOrderItemDto(OrderItemDto orderItemDto) {
        orderItemDtoList.add(orderItemDto);
    }
}
