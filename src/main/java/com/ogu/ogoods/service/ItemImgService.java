package com.ogu.ogoods.service;

import com.ogu.ogoods.entity.ItemImg;
import com.ogu.ogoods.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
// 의존성 자동 주입 (private final, @NotNull)
// -> itemImgRepository, fileService만 생성자 생성 됨
@Transactional
public class ItemImgService {
    @Value("${itemImgLocation}") // application.properties에 등록한 프로퍼티 값 불러옴
    private String itemImgLocation;
    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename(); // 파일의 기존 파일명을 oriImgName에 저장
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if(!StringUtils.isEmpty(oriImgName)) {
            // StringUtils.isEmpty : Null이면 true를 반환 -> ! 논리 부정 연산자
            // 파일명이 있다면
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            // uploadFile 메소드를 통해 새로운 파일명을 리턴 받아 imgName에 저장
            imgUrl = "/images/item/" + imgName;
        }

        // 상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
        if(!itemImgFile.isEmpty()) { // 상품 이미지 파일이 비어있지 않다면
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId) // 상품 이미지 번호를 이용하여 기존 이미지 조회
                                                     .orElseThrow(EntityNotFoundException::new);

            // 기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())) { // 기존 이미지의 이름이 비어있지 않다면
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName()); // 파일 삭제
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            // 상품 이미지 업로드
            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
            // 변경된 상품 이미지 정보 업데이트
            // savedItemImg 엔티티는 영속 상태 -> itemImgRepository.save() 호출하지 않고 update 쿼리 실행 됨
        }
    } // 상품 수정 - 이미지 수정
}
