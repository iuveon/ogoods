package com.ogu.ogoods.dto;

import com.ogu.ogoods.constant.ItemSellStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainItemDto {

    private Long id;
    private String itemNm;
    private String itemDetail;
    private String imgUrl;
    private Integer price;
    private ItemSellStatus itemSellStatus;

    @QueryProjection
    // DTO의 생성자에 어노테이션을 붙여 Querydsl에 의존하여 Q파일 생성 -> 컴파일 단계에서 에러 잡을 수 있음
    public MainItemDto(Long id, String itemNm, String itemDetail, String imgUrl, Integer price, ItemSellStatus itemSellStatus) {
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
        this.itemSellStatus = itemSellStatus;
    }
}
