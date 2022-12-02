package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

//    private LocalDateTime regTime;
//
//    private LocalDateTime updateTime;

    // 엔티티를 양방향 연관 관계로 설정하면 설정하면 객체의 참조는 둘인데 외래키는 하나이므로 둘 중 누가 외래키를 관리할지 정해야 합니다.
    // 연관 관계의 주인은 외래키가 있는 곳으로 설정
    // 연관 관계의 주인이 외래키를 관리(등록, 수정, 삭제)
    // 주인이 아닌 쪽은 연관 관계 매핑 시 mappedBy 속성의 값으로 연관 관계의 주인을 설정
    // 주인이 아닌 쪽은 읽기만 가능
    @OneToMany(mappedBy = "order" , cascade = CascadeType.ALL , orphanRemoval = true , fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) { // orderItems에는 주문 상품 정보들을 담아줍니다. orderItem 객체를 order 객체의 orderItems에 추가합니다.
        orderItems.add(orderItem);
        orderItem.setOrder(this); // Order 엔티티와 OrderItem 엔티티가 양방향 참조 관계 이므로, orderItem 객체에도 order 객체를 세팅합니다.
    }

    public static Order createOrder(Member member , List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member); // 상품을 주문한 회원의 정보를 세팅합니다.

        for(OrderItem orderItem : orderItemList){ // 상품 페이지에서는 1개의 상품을 주문하지만, 장바구니 페이지에서는 한 번에 여러 개의 상품을 주문할 수 있습니다.
            // 따라서 여러 개의 주문 상품을 담을 수 있도록 리스트 형태로 파라미터 값을 받으며 주문 객체에 orderItem 객체를 추가합니다.
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    public int getTotalPrice() { // 총 주문 금액을 구하는 메소드 입니다.
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems) {
            totalPrice = totalPrice + orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL;

        for(OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

}
