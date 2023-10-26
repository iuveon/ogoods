package com.ogu.ogoods.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartOrderDto {

    private Long cartItemId;
    private List<CartOrderDto> cartOrderDtoList;
    // 장바구니에서 여러 개의 상품 주문 가능 -> 자기 자신을 List로 갖고 있도록

}
