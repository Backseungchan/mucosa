package com.hanghae99.mocosa.layer.controller;

import com.hanghae99.mocosa.config.auth.UserDetailsImpl;
import com.hanghae99.mocosa.layer.dto.order.OrderHistoryResponseDto;
import com.hanghae99.mocosa.layer.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/users/orders")
    public ResponseEntity<Page<OrderHistoryResponseDto>> getOrderHistory(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam int page) {

        Page<OrderHistoryResponseDto> orderHistoryResponseDtoPage = orderService.getOrderHistory(userDetails, page);

        return new ResponseEntity<>(orderHistoryResponseDtoPage, HttpStatus.OK);
    }

}