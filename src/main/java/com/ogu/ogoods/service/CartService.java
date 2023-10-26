package com.ogu.ogoods.service;

import com.ogu.ogoods.dto.CartDetailDto;
import com.ogu.ogoods.dto.CartItemDto;
import com.ogu.ogoods.dto.CartOrderDto;
import com.ogu.ogoods.dto.OrderDto;
import com.ogu.ogoods.entity.*;
import com.ogu.ogoods.repository.CartItemRepository;
import com.ogu.ogoods.repository.CartRepository;
import com.ogu.ogoods.repository.ItemRepository;
import com.ogu.ogoods.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String email) {
        Item item = itemRepository.findById(cartItemDto.getItemId())
                                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email); // 현재 로그인한 회원 엔티티 조회하여 member로 저장

        Cart cart = cartRepository.findByMemberId(member.getId()); // 현재 로그인한 회원의 장바구니 엔티티 조회하여 cart에 저장
        if(cart == null) { // cart가 비어있다면 -> 아직 장바구니가 생성되지 않음
            cart = Cart.createCart(member); // 장바구니 엔티티 생성
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());
        // 현재 상품이 장바구니에 존재하는지 조회

        if(savedCartItem != null) { // savedCartItem이 null이 아니라면 -> 이미 장바구니에 존재하는 상품인 경우
            savedCartItem.addCount(cartItemDto.getCount()); // 장바구니 담을 수량만큼 add해줌
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            // CartItem 엔티티 생성
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());
        // 현재 로그인한 회원의 장바구니 엔티티 조회
        if(cart == null) { // 장바구니가 비어있다면
            return cartDetailDtoList; // 비어있는 리스트 반환
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return  cartDetailDtoList;
        // 장바구니에 담긴 상품 정보 조회하여 리턴
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email) {
        Member curMember = memberRepository.findByEmail(email); // 현재 로그인한 회원 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                                            .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember(); // 장바구니 상품 저장 회원 조회

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            // curMember와 savedMember가 같지 않다면
            return false; // false 리턴
        }
        return true; // 같다면 true 리턴
    }

    public void updateCartItemCount(Long cartItemId, int count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                                            .orElseThrow(EntityNotFoundException::new);
        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                                            .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email) {
        List<OrderDto> orderDtoList = new ArrayList<>();
        for(CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                                                .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);

        for(CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                                                .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
            // 주문한 상품은 장바구니에서 삭제되도록 함
        }
        return orderId;
    }

}
