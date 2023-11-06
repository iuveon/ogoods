package com.ogu.ogoods.dto;

import com.ogu.ogoods.constant.ItemSellStatus;
import com.ogu.ogoods.constant.NoticeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeSearchDTO { // 상품 조회 조건
    private String searchDateType;
    /*
    all : 게시글 등록일 전체
    1d : 최근 하루 동안 등록된 게시글
    1w : 최근 일주일 동안 등록된 게시글
    1m : 최근 한 달 동안 등록된 게시글
    6m : 최근 6개월 동안 등록된 게시글
    */
    private NoticeStatus noticeStatus; // 공지사항 상태 기준
    private String searchBy; // 공지사항 조회 유형 (title : 제목 / createdBy : 공지사항 등록자)
    private String searchQuery = ""; // 조회 검색어 저장 변수
}
