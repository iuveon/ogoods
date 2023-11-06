package com.ogu.ogoods.repository;

import com.ogu.ogoods.constant.NoticeStatus;
import com.ogu.ogoods.dto.*;
import com.ogu.ogoods.entity.*;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom{

    private JPAQueryFactory queryFactory;
    // JPAQueryFactory : Querydsl과 JPA를 이용하여 동적 쿼리 생성을 도와줌

    public NoticeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        // 생성자를 통해 JPAQueryFactory에 EntityManager 객체를 넣어줌
    }


    private BooleanExpression searchNoticeStatusEq(NoticeStatus noticeStatus) {
        return noticeStatus == null ? null : QNotice.notice.noticeStatus.eq(noticeStatus);
        // 삼항연산식사용 -> 상품 판매 상태가 null 이라면? null 반환, 아니라면 상품 판매 상태 일치 조건 반환
    }


    private BooleanExpression regDtsAfter(String searchDateType) {

        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if(StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        } // searchDateType에 따라서 현재 기준 이전 시간으로 세팅

        return QNotice.notice.regTime.after(dateTime); // 상품 등록일이 dateTime 이후인지 반환
    }


    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if(StringUtils.equals("title", searchBy)) { // searchBy 값이 title일 때
            return QNotice.notice.title.like("%" + searchQuery + "%"); // 검색어를 포함한 제목
        } else if(StringUtils.equals("createdBy", searchBy)) { // searchBy 값이 createdBy일 때
            return QNotice.notice.createdBy.like("%" + searchQuery + "%"); // 검색어를 포함한 작성자
        }
        return null;
    }


    @Override
    public Page<Notice> getAdminNoticePage(NoticeSearchDTO noticeSearchDTO, Pageable pageable) {

        QueryResults<Notice> results = queryFactory.selectFrom(QNotice.notice)
                .where(regDtsAfter(noticeSearchDTO.getSearchDateType()),
                        searchNoticeStatusEq(noticeSearchDTO.getNoticeStatus()),
                        searchByLike(noticeSearchDTO.getSearchBy(),
                                noticeSearchDTO.getSearchQuery()))
                // where 조건절을 이용하여 BooleanExpression 반환 조건문 입력
                // ,를 이용하면 and 조건 인식
                .orderBy(QNotice.notice.nno.desc())
                .offset(pageable.getOffset()) // 데이터를 가지고 올 시작 인덱스 지정
                .limit(pageable.getPageSize()) // 검색 결과에서 가지고 올 항목 개수 지정
                .fetchResults(); // 조회 리스트 및 전체 개수 반환 -> Querydsl 5버전에서 deprecated 됨
        List<Notice> content = results.getResults(); // 실제 항목 리스트 content로 저장
        long total = results.getTotal(); // 전체 항목 개수 total로 저장

        return new PageImpl<>(content, pageable, total);
    }


    private BooleanExpression itemNmLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QNotice.notice.title.like("%" + searchQuery + "%");
        // 검색어가 비어있다면 null, 아니라면 검색어가 포함되는 조회 조건 리턴
    }


    @Override
    public Page<MainNoticeDTO> getMainNoticePage(NoticeSearchDTO noticeSearchDTO, Pageable pageable) {

        QNotice notice = QNotice.notice;
        QNoticeImg noticeImg = QNoticeImg.noticeImg;

        QueryResults<MainNoticeDTO> results = queryFactory
                .select(new QMainNoticeDTO(notice.nno, notice.title, notice.content, noticeImg.imgUrl))
                // QMainItemDto 생성자에 값 입력 -> @QueryProjection을 사용하여 DTO 바로 조회
                // 엔티티 조회 후 DTO 변환 과정 단축
                .from(noticeImg)
                .join(noticeImg.notice, notice)
                // itemImg와 img 내부 조인 -> 교집합 결과를 반환함
                .where(notice.noticeStatus.ne(NoticeStatus.HIDDEN)) // 공지사항상태(NoticeStatus)가 HIDDEN 아닌 경우만
                .where(noticeImg.repImgYn.eq("Y")) // repImgYn (대표 이미지 여부)가 Y인 경우만
                .where(itemNmLike(noticeSearchDTO.getSearchQuery()))
                .orderBy(
                        notice.noticeStatus.asc(),  // NoticeStatus 오름차순(SELL - SOLD_OUT - RESERVE)
                        notice.nno.desc()              // notice.nno 내림차순
                )
                .offset(pageable.getOffset()) // getOffset : 선택한 요소의 좌표 값 가져오기
                .limit(pageable.getPageSize())
                .fetchResults();
        List<MainNoticeDTO> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
}
