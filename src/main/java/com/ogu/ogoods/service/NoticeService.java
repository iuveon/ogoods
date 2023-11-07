package com.ogu.ogoods.service;

import com.ogu.ogoods.dto.*;
import com.ogu.ogoods.entity.Notice;
import com.ogu.ogoods.entity.NoticeImg;
import com.ogu.ogoods.repository.NoticeImgRepository;
import com.ogu.ogoods.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeImgService noticeImgService;
    private final NoticeImgRepository noticeImgRepository;

    public Long saveNotice(NoticeFormDTO noticeFormDTO, List<MultipartFile> noticeImgFileList) throws Exception {

        // 상품 등록
        Notice notice = noticeFormDTO.createNotice(); // noticeDTO를 entity로 변환하는 메소드 사용하여 notice으로 저장
        noticeRepository.save(notice); // noticeRepository를 이용하여 DB에 저장

        // 이미지 등록
        for(int i = 0; i < noticeImgFileList.size(); i++) { // 상품 이미지 파일 목록 순회
            NoticeImg noticeImg = new NoticeImg();
            noticeImg.setNotice(notice);
            if(i == 0) // 첫 번째 이미지인 경우
                noticeImg.setRepImgYn("Y"); // 대표이미지 여부를 Y로 설정
            else // 아니라면
                noticeImg.setRepImgYn("N"); // 대표이미지 여부를 N으로 설정
            noticeImgService.saveNoticeImg(noticeImg, noticeImgFileList.get(i));
        }
        return notice.getNno();
    }

    @Transactional(readOnly = true) // 데이터 변경 없을 때 -> 읽기 전용 트랜잭션 수행으로 성능 향상
    public NoticeFormDTO getNoticeDtl(Long nno){
        List<NoticeImg> noticeImgList = noticeImgRepository.findByIdOrderByIdAsc(nno);
        // 공지사항의 이미지 리스트 조회
        List<NoticeImgDTO> noticeImgDTOList = new ArrayList<>();
        for (NoticeImg noticeImg : noticeImgList) {
            NoticeImgDTO noticeImgDto = NoticeImgDTO.of(noticeImg);
            noticeImgDTOList.add(noticeImgDto);
        } // 엔티티 List를 DTO List로 매핑해주는 for문

        Notice notice = noticeRepository.findById(nno)
                .orElseThrow(EntityNotFoundException::new);
        NoticeFormDTO noticeFormDto = NoticeFormDTO.of(notice);
        noticeFormDto.setNoticeImgDTOList(noticeImgDTOList);
        return noticeFormDto;
    }

    public Long updateNotice(NoticeFormDTO noticeFormDTO, List<MultipartFile> noticeImgFileList) throws Exception {
        //공지사항 수정
        Notice notice = noticeRepository.findById(noticeFormDTO.getNno())
                .orElseThrow(EntityNotFoundException::new);

        notice.updateNotice(noticeFormDTO);
        List<Long> noticeImgIds = noticeFormDTO.getNoticeImgIds();


        //이미지 등록
        for(int i = 0; i < noticeImgFileList.size(); i++){
            noticeImgService.updateNoticeImg(noticeImgIds.get(i),
                    noticeImgFileList.get(i));
        }
        return notice.getNno();
    }

    @Transactional(readOnly = true) // 데이터 수정 없으므로 최적화를 위해 readOnly 옵션 넣기
    public Page<Notice> getAdminNoticePage(NoticeSearchDTO noticeSearchDTO, Pageable pageable) {
        return noticeRepository.getAdminNoticePage(noticeSearchDTO, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainNoticeDTO> getMainNoticePage(NoticeSearchDTO noticeSearchDTO, Pageable pageable) {
        return noticeRepository.getMainNoticePage(noticeSearchDTO, pageable);
    }

}
