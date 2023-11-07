package com.ogu.ogoods.controller;

import com.ogu.ogoods.dto.NoticeFormDTO;
import com.ogu.ogoods.dto.NoticeSearchDTO;
import com.ogu.ogoods.entity.Notice;
import com.ogu.ogoods.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping(value = "/admin/notice/new") // http://localhost/admin/notice/new
    public String noticeForm(Model model) {
        model.addAttribute("noticeFormDTO", new NoticeFormDTO());
        return "/notice/noticeForm";
    }

    @PostMapping(value = "/admin/notice/new")
    public String noticeNew(@Valid NoticeFormDTO noticeFormDTO, BindingResult bindingResult,
                          Model model, @RequestParam("noticeImgFile")List<MultipartFile> noticeImgFileList) {
        // @Valid : RequestBody로 들어오는 객체 검증 -> ItemFormDto의 @NotBlank, @NotNull을 검증해 줌
        // BindingResult : 검증 오류가 발생 시 오류를 보관하고 있는 역할
        if(bindingResult.hasErrors()) { // 검증 오류 발생 시
            return "notice/noticeForm"; // 상품 등록 페이지로 리턴
        }

        if(noticeImgFileList.get(0).isEmpty() && noticeFormDTO.getNno() == null) { // 첫 번째 이미지가 비어있다면
            model.addAttribute("errorMessage", "첫번째 이미지는 필수입니다.");
            return "notice/noticeForm"; // 에러 메세지와 함께 공지사항 등록 페이지로 리턴
        }

        try {
            noticeService.saveNotice(noticeFormDTO, noticeImgFileList); // 게시글 등록
        } catch (Exception e) {
            model.addAttribute("errorMessage", "공지사항 등록 중 에러가 발생하였습니다.");
            return "notice/noticeForm"; // 에러 메세지와 함께 공지사항 등록 페이지로 리턴
        }
        return "redirect:/"; // 완료되면 메인페이지로 리다이렉트 시킴
    }

    @GetMapping(value = "/admin/notice/{nno}") // http://localhost/admin/notice/공지번호
    public String noticeDtl(@PathVariable("nno") Long nno, Model model) {
        try {
            NoticeFormDTO noticeFormDTO = noticeService.getNoticeDtl(nno);
            model.addAttribute("noticeFormDTO", noticeFormDTO);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 게시글입니다.");
            model.addAttribute("noticeFormDTO", new NoticeFormDTO());
            return "notice/noticeForm";
            // 존재하지 않는 엔티티일 경우 에러메세지와 함께 공지사항 등록 페이지로 리턴
        }
        return "notice/noticeForm";
    }

    @PostMapping(value = "/admin/notice/{nno}")
    public String noticeUpdate(@Valid NoticeFormDTO noticeFormDTO, BindingResult bindingResult,
                             @RequestParam("noticeImgFile") List<MultipartFile> noticeImgFileList, Model model) {
        // @Valid : RequestBody로 들어오는 객체 검증 -> ItemFormDto의 @NotBlank, @NotNull을 검증해 줌
        // BindingResult : 검증 오류가 발생 시 오류를 보관하고 있는 역할
        if(bindingResult.hasErrors()) {
            return "notice/noticeForm";
        }

        if(noticeImgFileList.get(0).isEmpty() && noticeFormDTO.getNno() == null) { // 첫 번째 상품 이미지가 비어있다면
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수입니다.");
            return "notice/noticeForm";
        }

        try {
            noticeService.updateNotice(noticeFormDTO, noticeImgFileList); // 게시글 수정
        } catch (Exception e) {
            model.addAttribute("errorMessage", "공지사항 수정 중 에러가 발생하였습니다.");
            return "notice/noticeForm";
        }
        return "redirect:/"; // 완료되면 메인페이지로 리다이렉트 시킴
    }

    @GetMapping(value = {"/notices", "/notices/{page}"})
    // 배열을 통해 http://localhost/admin/notices http://localhost/admin/페이지번호 모두 반응
    public String noticeManage(NoticeSearchDTO noticeSearchDTO, @PathVariable("page") Optional<Integer> page, Model model) {
        // @PathVariable : URL경로에서 변수 값 추출하여 사용 가능
        // Optional -> NPE 방지 wrapper 클래스
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        // PageRequest.of(pageNumber, pageSize) -> 가져올 페이지 번호, 한 페이지 당 가져올 항목의 수
        // isPresent : Optional 객체에 값이 있다면 true 반환, 없다면 false 반환
        // URL 경로에 페이지 번호가 있다면? 해당 페이지 조회, 아니라면 0페이지 (첫 번째 페이지) 조회
        Page<Notice> notices = noticeService.getAdminNoticePage(noticeSearchDTO, pageable);
        model.addAttribute("notices", notices); // 상품 데이터와 페이징 정보 뷰로 전달
        model.addAttribute("noticeSearchDTO", noticeSearchDTO);
        // 페이지 전환 시 기존 검색 조건 유지할 수 있도록 뷰에 다시 전달
        model.addAttribute("maxPage", 5); // 최대 페이지 번호 개수
        return "notice/noticeList";
    }

    @GetMapping(value = "/notice/{nno}") // http://localhost/notice/공지번호
    public String noticeDtl(Model model, @PathVariable("nno") Long nno) {
        NoticeFormDTO noticeFormDTO = noticeService.getNoticeDtl(nno);
        model.addAttribute("notice", noticeFormDTO);
        return "notice/noticeDtl";
    }

}
