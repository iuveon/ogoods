package com.ogu.ogoods.controller;

import com.ogu.ogoods.dto.CartDetailDto;
import com.ogu.ogoods.dto.CartItemDto;
import com.ogu.ogoods.dto.CartOrderDto;
import com.ogu.ogoods.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                                              BindingResult bindingResult, Principal principal) {
        // @Valid를 이용하여 검증 -> BindingResult : 검증 오류 담는 객체
        // Principal : 현재 로그인한 회원의 사용자 정보를 담고 있는 객체

        if(bindingResult.hasErrors()) { // cartItemDto 데이터 바인딩 시 에러가 있다면
            StringBuilder sb = new StringBuilder(); // 문자열을 변경 가능하도록 메소드 제공
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            // 유효성 검사 오류를 받아서 fieldErrors에 리스트 형태로 저장
            for(FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
                // StringBuilder.append : 문자열 추가 -> 검사 오류를 결합
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
            // 오류 메세지와 함께 400 ERROR (BAD_REQUEST) 리턴
        }

        String email = principal.getName(); // 사용자의 ID(이메일)를 받아서 email로 저장
        Long cartItemId;

        try {
            cartItemId = cartService.addCart(cartItemDto, email);
            // 장바구니에 상품 담기 로직 호출
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
            // 예외 발생 시 오류 메세지와 함께 400 ERROR (BAD_REQUEST) 리턴
        }

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
        // 장바구니 상품 아이디와 함께 요청 성공 HTTP 응답 상태 코드 리턴

    }

    @GetMapping(value = "/cart") // http://localhost/cart
    public String orderHist(Principal principal, Model model) {
        List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());
        model.addAttribute("cartItems", cartDetailList);
        return "cart/cartList";
    }

    @PatchMapping(value = "/cartItem/{cartItemId}")
    // @PatchMapping : 업데이트 요청 시 일부 데이터만 수정 가능
    // Put과 달리 리소스의 모든 필드 변경할 필요 없음 -> 장바구니 상품의 수량만 변경
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       int count, Principal principal) {
        if(count <= 0) { // 장바구니 상품 개수가 0개 이하로 요청할 경우
            return new ResponseEntity<String>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
            // 메세지와 함께 400 ERROR(BAD_REQUEST) 리턴
        } else if(!cartService.validateCartItem(cartItemId, principal.getName())) {
            // 수정 권한 확인하여 true가 아니라면 -> 현재 로그인한 회원과 장바구니 상품 저장 회원이 다르다면
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
            // 메세지와 함께 403 ERROR(FORBIDDEN) 리턴
        }

        cartService.updateCartItemCount(cartItemId, count); // 장바구니 상품 개수 업데이트
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
        // 메세지와 함께 요청 성공 HTTP 응답 상태 코드 리턴
    }

    @DeleteMapping(value = "/cartItem/{cartItemId}")
    // @DeleteMapping : 서버에서 특정 리소스를 삭제할 때 사용
    public @ResponseBody ResponseEntity deleteCartItem (@PathVariable("cartItemId") Long cartItemId,
                                                        Principal principal) {
        if(!cartService.validateCartItem(cartItemId, principal.getName())) {
            // 수정 권한 확인하여 true가 아니라면 -> 현재 로그인한 회원과 장바구니 상품 저장 회원이 다르다면
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
            // 메세지와 함께 403 ERROR(FORBIDDEN) 리턴
        }

        cartService.deleteCartItem(cartItemId); // 장바구니 상품 삭제
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
        // 메세지와 함께 요청 성공 HTTP 응답 상태 코드 리턴
    }

    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal) {
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0) { // 주문할 상품 선택되지 않았다면
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
            // 메세지와 함께 403 ERROR(FORBIDDEN) 리턴
        }

        for(CartOrderDto cartOrder : cartOrderDtoList) {
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())) {
                // 권한 확인하여 true가 아니라면 -> 현재 로그인한 회원과 장바구니 상품 저장 회원이 다르다면
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
                // 메세지와 함께 403 ERROR(FORBIDDEN) 리턴
            }
        }

        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
        // 생성한 주문 번호와 함께 요청 성공 HTTP 응답 상태 코드 리턴
    }

}
