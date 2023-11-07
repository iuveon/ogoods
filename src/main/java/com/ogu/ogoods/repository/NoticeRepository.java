package com.ogu.ogoods.repository;

import com.ogu.ogoods.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long>, QuerydslPredicateExecutor<Notice>, NoticeRepositoryCustom{

        List<Notice> findByNno(String title); // 제목으로 찾기
        List<Notice> findByNnoOrContent(String title, String content); // 제목 또는 내용으로 찾기


// @Query 어노테이션 이용 - JPQL 작성 쿼리문
@Query("select n from Notice n where n.content like %:content% order by n.nno desc")
    List<Notice> findByContent(@Param("content") String content);
        // 내용 찾아서 공지번호 내림차순 정렬
}
