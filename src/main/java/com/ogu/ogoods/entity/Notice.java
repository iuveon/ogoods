package com.ogu.ogoods.entity;

import com.ogu.ogoods.constant.NoticeStatus;
import com.ogu.ogoods.dto.NoticeFormDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name="notice")
@Getter
@Setter
@ToString
public class Notice extends BaseEntity{

    @Id
    @Column(name="nno")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nno; // 공지번호

    @Column(nullable = false, length = 50)
    private String title; // 제목

    @Lob
    private String content; // 내용

    @Enumerated(EnumType.STRING) // Enum 타입 매핑(EnumType.STRING : Enum이름을 column에 저장
    private NoticeStatus noticeStatus; // 공지사항 상태


    public void updateNotice(NoticeFormDTO noticeFormDTO) {
        //수정은 내용과 공지사항 상태만 가능하도록 설정
        this.content = noticeFormDTO.getContent();
        this.noticeStatus = noticeFormDTO.getNoticeStatus();
    }

}
