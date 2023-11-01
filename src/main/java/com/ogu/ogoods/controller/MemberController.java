package com.ogu.ogoods.controller;

import com.ogu.ogoods.dto.MemberFormDto;
import com.ogu.ogoods.entity.Member;
import com.ogu.ogoods.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@RequestMapping("/members") // http://localhost/members/*
@Controller // 컨트롤러 명시
@RequiredArgsConstructor // 생성자 자동 주입 (@NotNull, private final 필드)
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/new") // http://localhost/members/new
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {
        // @Valid : 유효성 검사 -> 검사 후 결과는 BindingResult에 담아줌
        if(bindingResult.hasErrors()) { // Error가 존재한다면
            return "member/memberForm"; // 회원가입 페이지로 보냄
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage()); // 중복 회원 가입 예외 발생 시 에러 메세지 전달
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember() {
        return "/member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }

    @GetMapping(value =  "/checkId")
    public ResponseEntity<String> checkIdDuplication(@RequestParam("mid") String mid) {
        /*if (mid == null || mid.isEmpty()) { // 아이디가 비어있는 경우
            return ResponseEntity.ok("exists");
        }*/

        if (memberService.existsByMid(mid)) {
            return ResponseEntity.ok("exists"); // 이미 가입된 아이디
        } else {
            return ResponseEntity.ok("available"); // 사용 가능한 아이디
        }
    }

}
