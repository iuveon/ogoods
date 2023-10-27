package com.ogu.ogoods.service;

import com.ogu.ogoods.dto.OrderDto;
import com.ogu.ogoods.dto.OrderHistDto;
import com.ogu.ogoods.dto.OrderItemDto;
import com.ogu.ogoods.entity.*;
import com.ogu.ogoods.repository.ItemImgRepository;
import com.ogu.ogoods.repository.ItemRepository;
import com.ogu.ogoods.repository.MemberRepository;
import com.ogu.ogoods.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDto orderDto, String mid) {
        Item item = itemRepository.findById(orderDto.getItemId()) // 주문할 상품 조회
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByMid(mid); // 로그인한 회원의 이메일을 통해 회원 정보 조회

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount()); // OrderItem 엔티티 생성
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList); // Order 엔티티 생성
        orderRepository.save(order); // Order 엔티티 저장

        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String mid, Pageable pageable) {
        List<Order> orders = orderRepository.findOrders(mid, pageable); // 주문 목록 조회
        Long totalCount = orderRepository.countOrder(mid); // 주문 총 개수 구하기

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for(Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for(OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount); // 페이지 구현 객체를 생성하여 반환
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String mid) {
        Member curMember = memberRepository.findByMid(mid); // 현재 로그인한 사용자 curMember로 저장
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember(); // 주문 생성한 회원 savedMember로 저장

        if(!StringUtils.equals(curMember.getMid(), savedMember.getMid())) {
            // curMember와 savedMember가 같지 않다면
            return false; // false 리턴
        }
        return true; // 아니라면 true 리턴
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                                     .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
        // 주문 취소 상태가 되면 변경 감지 기능에 의해 트랜잭션 끝날 때 update 쿼리 실행
    }

    public Long orders(List<OrderDto> orderDtoList, String mid) {
        Member member = memberRepository.findByMid(mid);
        List<OrderItem> orderItemList = new ArrayList<>();

        for(OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }
}
