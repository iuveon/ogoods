package com.ogu.ogoods.controller;

import com.ogu.ogoods.dto.OrderDto;
import com.ogu.ogoods.dto.OrderHistDto;
import com.ogu.ogoods.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order (@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult, Principal principal) {
        // @Valid로 객체에 대한 검증을 한 후 -> BindingResult 객체에 검증 결과를 담음
        // Principal : 현재 로그인한 사용자의 정보를 나타내는 객체
        if(bindingResult.hasErrors()) { // OrderDto 객체 바인딩 시 에러가 있다면
            StringBuilder sb = new StringBuilder();
            // StringBuilder : 문자열을 추가, 삭제, 변경할 수 있는 메서드 제공
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
                // .append : StringBuilder 변수 뒤에 문자열을 추가
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
            // 에러 정보를 문자열로 만들고, Http응답으로 에러코드 리턴
        }

        String mid = principal.getName(); // 현재 로그인한 회원의 식별자를 가져와서 mid로 저장
        Long orderId;

        try {
            orderId = orderService.order(orderDto, mid); // 주문 로직 호출 후 반환 받은 주문번호를 orderId에 저장
        } catch(Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
        // 주문 성공 시 주문번호와 함께 요청 성공 HTTP 응삽 상태 코드를 리턴
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    // http://localhost/orders, http://localhost/orders/페이지번호
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);

        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders", ordersHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder (@PathVariable("orderId") Long orderId, Principal principal) {
        // Principal : 현재 로그인한 사용자의 정보
        if(!orderService.validateOrder(orderId, principal.getName())) {
            // 로그인사용자와 주문 생성 회원이 같지 않다면 false리턴 -> 논리 부정연산자로 true 값 됨
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
            // 주문 취소 권한이 없으면 메세지와 함께 FORBIDDEN(403 ERROR) 리턴
        }
        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
