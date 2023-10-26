package com.ogu.ogoods.dto;

import com.ogu.ogoods.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {

    //  필드
    private String itemNm; // 상품명
    private int count; // 주문 수량
    private int orderPrice; // 주문 금액
    private String imgUrl; // 상품 이미지 경로

    // 생성자
    public OrderItemDto(OrderItem orderItem, String imgUrl) {
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
    }

}
