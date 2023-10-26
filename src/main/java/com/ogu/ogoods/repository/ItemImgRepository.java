package com.ogu.ogoods.repository;

import com.ogu.ogoods.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);
    // 쿼리메소드 -> Id를 기준으로 오름차순 정려하여 ItemId를 찾음

    ItemImg findByItemIdAndRepImgYn(Long itemId, String repImgYn);
    // 쿼리메소드 -> 상품 대표 이미지 찾기
}
