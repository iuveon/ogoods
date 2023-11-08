package com.ogu.ogoods.dto;

import com.ogu.ogoods.constant.ItemSellStatus;
import com.ogu.ogoods.constant.NoticeStatus;
import com.ogu.ogoods.entity.Item;
import com.ogu.ogoods.entity.Notice;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NoticeFormDTO {

    private Long nno;

    @NotBlank(message = "제목을 입력하세요")
    private String title;

    private String content;

    private NoticeStatus noticeStatus;

    private List<NoticeImgDTO> noticeImgDTOList = new ArrayList<>();
    // 상품 저장 후 수정 시 이미지 정보 저장 리스트

    private List<Long> noticeImgIds = new ArrayList<>();//?
    // 상품 이미지 아이디 저장 리스트 -> 수정 시 이미지 아이디 담는 용도

    private static ModelMapper modelMapper = new ModelMapper();

    public Notice createNotice() { // DTO -> Entity
        return modelMapper.map(this, Notice.class);
    }

    public static NoticeFormDTO of(Notice notice) { // Entity -> DTO
        return modelMapper.map(notice, NoticeFormDTO.class);
    }

}

