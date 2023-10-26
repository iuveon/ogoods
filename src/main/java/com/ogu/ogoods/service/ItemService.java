package com.ogu.ogoods.service;

import com.ogu.ogoods.dto.ItemFormDto;
import com.ogu.ogoods.dto.ItemImgDto;
import com.ogu.ogoods.dto.ItemSearchDto;
import com.ogu.ogoods.dto.MainItemDto;
import com.ogu.ogoods.entity.Item;
import com.ogu.ogoods.entity.ItemImg;
import com.ogu.ogoods.repository.ItemImgRepository;
import com.ogu.ogoods.repository.ItemRepository;
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
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        // 상품 등록
        Item item = itemFormDto.createItem(); // itemDTO를 entity로 변환하는 메소드 사용하여 item으로 저장
        itemRepository.save(item); // itemRepository를 이용하여 DB에 저장

        // 이미지 등록
        for(int i = 0; i < itemImgFileList.size(); i++) { // 상품 이미지 파일 목록 순회
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i == 0) // 첫 번째 이미지인 경우
                itemImg.setRepImgYn("Y"); // 대표이미지 여부를 Y로 설정
            else // 아니라면
                itemImg.setRepImgYn("N"); // 대표이미지 여부를 N으로 설정
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true) // 데이터 변경 없을 때 -> 읽기 전용 트랜잭션 수행으로 성능 향상
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        // 상품의 이미지 리스트 조회
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            // ItemImgDto.of() 메소드 -> ModelMapper를 사용하여 엔티티를 DTO로 매핑하고, 매핑된 DTO 객체를 반환
            // itemImgList 리스트를 순회하면서 엔티티를 DTO로 매핑하여 itemImgDto에 저장
            itemImgDtoList.add(itemImgDto); // itemImgDtoList에 추가
        } // 엔티티 List를 DTO List로 매핑해주는 for문

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        //상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())
                                    .orElseThrow(EntityNotFoundException::new);
        // itemId 이용하여 엔티티 조회
        item.updateItem(itemFormDto); // 뷰에서 전달받은 itemFormDto 이용하여 Item 엔티티 업데이트
        List<Long> itemImgIds = itemFormDto.getItemImgIds();
        // Item 이미지 ID 리스트를 조회하여 itemImgIds로 저장

        //이미지 등록
        for(int i = 0; i < itemImgFileList.size(); i++){
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true) // 데이터 수정 없으므로 최적화를 위해 readOnly 옵션 넣기
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    } // 상품 데이터 조회 메소드

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }

}
