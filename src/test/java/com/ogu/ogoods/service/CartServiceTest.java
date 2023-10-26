package com.ogu.ogoods.service;

import com.ogu.ogoods.constant.ItemSellStatus;
import com.ogu.ogoods.dto.CartItemDto;
import com.ogu.ogoods.entity.CartItem;
import com.ogu.ogoods.entity.Item;
import com.ogu.ogoods.entity.Member;
import com.ogu.ogoods.repository.CartItemRepository;
import com.ogu.ogoods.repository.ItemRepository;
import com.ogu.ogoods.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class CartServiceTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CartService cartService;
    @Autowired
    CartItemRepository cartItemRepository;

    public Item saveItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    public Member saveMember() {
        Member member = new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("장바구니 담기 테스트")
    public void addCart() {
        Item item = saveItem();
        Member member = saveMember();

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCount(5);
        cartItemDto.setItemId(item.getId());

        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());
        // 장바구니 담기 로직 호출 -> 리턴된 장바구니 상품 아이디를 cartItemId에 저장

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                                            .orElseThrow(EntityNotFoundException::new);
        // 장바구니 상품 아이디를 이용하여 장바구니 상품 정보 조회

        assertEquals(item.getId(), cartItem.getItem().getId());
        assertEquals(cartItemDto.getCount(), cartItem.getCount());
        // assertEquals : 두 객체의 값이 같은지
    }
}
