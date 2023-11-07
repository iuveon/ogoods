package com.ogu.ogoods.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "noticeimage")
@Getter
@Setter
public class NoticeImg extends BaseEntity {

    @Id
    @Column(name = "notice_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgName; // 이미지 파일명
    private String oriImgName; // 원본 이미지 파일명
    private String imgUrl; // 이미지 조회 경로
    private String repImgYn; // 대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY) // Item 엔티티와 다대일 단방향 매핑
    @JoinColumn(name = "nno")
    private Notice notice;

    public void updateNoticeImg(String oriImgName, String imgName, String imgUrl) {
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }

}
