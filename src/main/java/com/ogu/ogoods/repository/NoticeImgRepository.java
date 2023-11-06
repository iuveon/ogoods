package com.ogu.ogoods.repository;

import com.ogu.ogoods.entity.NoticeImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeImgRepository extends JpaRepository<NoticeImg, Long> {

    List<NoticeImg> findByIdOrderByIdAsc(Long id);
    // 쿼리메소드 -> nno를 기준으로 오름차순 정렬하여 ItemId를 찾음

    NoticeImg findByIdAndRepImgYn(Long id, String repImgYn);
    // 쿼리메소드 -> 상품 대표 이미지 찾기
}
