package com.beyond.order_system.ordering.service;

import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.member.repository.MemberRepository;
import com.beyond.order_system.ordering.domain.OrderDetail;
import com.beyond.order_system.ordering.domain.Ordering;
import com.beyond.order_system.ordering.dto.OrderingReqDto;
import com.beyond.order_system.ordering.repository.OrderingRepository;
import com.beyond.order_system.product.domain.Product;
import com.beyond.order_system.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderingService {
    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, ProductRepository productRepository) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    public Ordering orderingCreate(OrderingReqDto dto) {
        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(()->new EntityNotFoundException("member not found"));

        Ordering ordering = Ordering.builder()
                .member(member)
                .build();

        Ordering savedOrdering = orderingRepository.save(ordering);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        for(OrderingReqDto.OrderDetailDto orderDetailDto : dto.getOrderDetails()){
            Product product = productRepository.findById(orderDetailDto.getProductId()).orElseThrow(()->new EntityNotFoundException("product not found"));
            OrderDetail orderDetail = OrderDetail.builder()
                    .ordering(ordering)
                    .product(product)
                    .quantity(orderDetailDto.getProductCount())
                    .build();
            ordering.getOrderDetails().add(orderDetail);
        }



        return savedOrdering;

    }
}
