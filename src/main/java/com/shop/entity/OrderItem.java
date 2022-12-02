package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;

    private int count;

//    private LocalDateTime regTime;
//
//    private LocalDateTime updateTime;

    public static OrderItem createOrderItem(Item item , int count) {

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item); // 주문할 상품과 주문 수량을 세팅합니다.
        orderItem.setCount(count); // 주문할 상품과 주문 수량을 세팅합니다.
        orderItem.setOrderPrice(item.getPrice()); // 현재 시간 기준으로 상품 가격을 주문 가격으로 세팅합니다. 상품 가격은 시간에 따라서 달라질 수 있습니다. 또한 쿠폰이나 할인을 적용하는 케이스들도 있지만 여기서는 고려하지 않겠습니다.

        item.removeStock(count);
        return orderItem;
    }

    public int getTotalPrice() { // 주문 가격과 주문 수량을 곱해서 해당 상품을 주문한 총 가격을 계산하는 메소드입니다.
        return orderPrice*count;
    }

    public void cancel() {
        this.getItem().addStock(count);
    }


}
