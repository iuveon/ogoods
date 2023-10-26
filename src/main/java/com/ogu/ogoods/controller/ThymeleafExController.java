package com.ogu.ogoods.controller;

import com.ogu.ogoods.dto.ItemDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping(value="/thymeleaf")
public class ThymeleafExController {

    @GetMapping(value="/ex02")
    public String thymeleafExample02(Model model) {
        ItemDto itemDto = new ItemDto(); // DTO 객체 생성
        itemDto.setItemDetail("상품 상세 설명");
        itemDto.setItemNm("테스트 상품1");
        itemDto.setPrice(10000);
        itemDto.setRegTime(LocalDateTime.now());

        model.addAttribute("itemDto", itemDto);
        // 모델에 객체 데이터를 'itemDto' 라는 이름으로 담아 보냄
        return "thymeleafEx/thymeleafEx02"; // thymeleafEx02.html으로 리턴
    }

    @GetMapping(value="/ex07")
    public String thymeleafExample07() {
        return "thymeleafEx/thymeleafEx07";
    }
}
