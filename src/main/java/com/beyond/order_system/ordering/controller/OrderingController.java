package com.beyond.order_system.ordering.controller;

import com.beyond.order_system.ordering.domain.Ordering;
import com.beyond.order_system.ordering.dto.OrderingReqDto;
import com.beyond.order_system.ordering.dto.OrderingResDto;
import com.beyond.order_system.ordering.service.OrderingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/ordering")
public class OrderingController {
    private final OrderingService orderingService;


    public OrderingController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }

    @PostMapping("/create")
    public Ordering orderingCreate(@RequestBody OrderingReqDto orderingReqDto){
        return orderingService.orderingCreate(orderingReqDto);

    }
}
