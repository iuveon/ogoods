package com.ogu.ogoods.dto;

import com.ogu.ogoods.entity.ItemImg;
import com.ogu.ogoods.entity.NoticeImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class NoticeImgDTO {
    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn; //대표이미지 여부
    private static ModelMapper modelMapper = new ModelMapper(); // ModelMapper 객체 추가

    public static NoticeImgDTO of(NoticeImg noticeImg) {
        // ModelMapper를 사용하여 엔티티를 DTO로 매핑하고, 매핑된 DTO 객체를 반환
        return modelMapper.map(noticeImg, NoticeImgDTO.class);
    }
}
