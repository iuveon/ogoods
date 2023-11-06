package com.ogu.ogoods.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoticeDTO {

    private Long nno;
    private String title;
    private String content;
    private String noticeStatCd;//?
    private LocalDateTime regTime;
    private LocalDateTime updateTime;

}
