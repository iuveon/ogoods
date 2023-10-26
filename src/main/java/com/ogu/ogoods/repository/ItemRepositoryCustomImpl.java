package com.ogu.ogoods.repository;

import com.ogu.ogoods.constant.ItemSellStatus;
import com.ogu.ogoods.dto.ItemSearchDto;
import com.ogu.ogoods.dto.MainItemDto;
import com.ogu.ogoods.dto.QMainItemDto;
import com.ogu.ogoods.entity.Item;
import com.ogu.ogoods.entity.QItem;
import com.ogu.ogoods.entity.QItemImg;
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

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private JPAQueryFactory queryFactory;
    // JPAQueryFactory : Querydsl과 JPA를 이용하여 동적 쿼리 생성을 도와줌

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        // 생성자를 통해 JPAQueryFactory에 EntityManager 객체를 넣어줌
    }

    // BooleanExpression -> 동적 쿼리의 조건식으로 null을 반환하게 될 경우 자동으로 조건절에서 제거

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
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

        return QItem.item.regTime.after(dateTime); // 상품 등록일이 dateTime 이후인지 반환
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if(StringUtils.equals("itemNm", searchBy)) { // searchBy 값이 itemNm일 때
            return QItem.item.itemNm.like("%" + searchQuery + "%"); // 검색어를 포함한 상품명
        } else if(StringUtils.equals("createdBy", searchBy)) { // searchBy 값이 createdBy일 때
            return QItem.item.createdBy.like("%" + searchQuery + "%"); // 검색어를 포함한 작성자 아이디
        }
        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QueryResults<Item> results = queryFactory.selectFrom(QItem.item)
                                                 .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                                                         searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                                                         searchByLike(itemSearchDto.getSearchBy(),
                                                                      itemSearchDto.getSearchQuery()))
                                                // where 조건절을 이용하여 BooleanExpression 반환 조건문 입력
                                                // ,를 이용하면 and 조건 인식
                                                 .orderBy(QItem.item.id.desc())
                                                 .offset(pageable.getOffset()) // 데이터를 가지고 올 시작 인덱스 지정
                                                 .limit(pageable.getPageSize()) // 검색 결과에서 가지고 올 항목 개수 지정
                                                 .fetchResults(); // 조회 리스트 및 전체 개수 반환 -> Querydsl 5버전에서 deprecated 됨
        List<Item> content = results.getResults(); // 실제 항목 리스트 content로 저장
        long total = results.getTotal(); // 전체 항목 개수 total로 저장
        return new PageImpl<>(content, pageable, total); // 페이지 객체 생성 후 반환
    }

    private BooleanExpression itemNmLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
        // 검색어가 비어있다면 null, 아니라면 검색어가 포함되는 조회 조건 리턴
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = queryFactory
                .select(new QMainItemDto(item.id, item.itemNm, item.itemDetail, itemImg.imgUrl, item.price))
                // QMainItemDto 생성자에 값 입력 -> @QueryProjection을 사용하여 DTO 바로 조회
                // 엔티티 조회 후 DTO 변환 과정 단축
                .from(itemImg)
                .join(itemImg.item, item)
                // itemImg와 img 내부 조인 -> 교집합 결과를 반환함
                //.where(item.itemSellStatus.eq(ItemSellStatus.SELL)) // 상품판매여부(ItemSellStatus)가 SELL인 경우만
                .where(item.itemSellStatus.ne(ItemSellStatus.RESERVE)) // 상품판매여부(ItemSellStatus)가 RESERVE 아닌 경우만
                .where(itemImg.repImgYn.eq("Y")) // repImgYn (대표 이미지 여부)가 Y인 경우만
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                //.orderBy(item.id.desc())
                .orderBy(
                        item.itemSellStatus.asc(),  // ItemSellStatus 오름차순(SELL - SOLD_OUT - RESERVE)
                        item.id.desc()              // item.id 내림차순
                )
                .offset(pageable.getOffset()) // getOffset : 선택한 요소의 좌표 값 가져오기
                .limit(pageable.getPageSize())
                .fetchResults();
        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }
}