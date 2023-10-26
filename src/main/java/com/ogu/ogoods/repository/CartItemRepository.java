package com.ogu.ogoods.repository;

import com.ogu.ogoods.dto.CartDetailDto;
import com.ogu.ogoods.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    @Query("select new com.ogu.ogoods.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
            "from CartItem ci, ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " +
            "and im.repImgYn = 'Y' " +
            "order by ci.regTime desc")
    /* new com.ogu.ogoods.dto.CartDetailDto
    DTO를 직접 조회하기 위해서 new를 통해 생성자를 통해 객체 반환
    JPQL에서는 패키지와 함께 클래스명을 작성해줘야 함
    생성자 파라미터 순서는 DTO 클래스 명시 순으로 넣을 것 */
    List<CartDetailDto> findCartDetailDtoList(Long cartId);

}
