package com.ogu.ogoods.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainNoticeDTO {

    private Long nno;
    private String title;
    private String content;
    private String imgUrl;

    @QueryProjection
    // DTO의 생성자에 어노테이션을 붙여 Querydsl에 의존하여 Q파일 생성 -> 컴파일 단계에서 에러 잡을 수 있음
    public MainNoticeDTO(Long nno, String title, String content, String imgUrl) {
        this.nno = nno;
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
    }
}
