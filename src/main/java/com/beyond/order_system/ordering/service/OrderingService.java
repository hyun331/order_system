package com.beyond.order_system.ordering.service;

import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.member.repository.MemberRepository;
import com.beyond.order_system.ordering.domain.OrderDetail;
import com.beyond.order_system.ordering.domain.OrderStatus;
import com.beyond.order_system.ordering.domain.Ordering;
import com.beyond.order_system.ordering.dto.OrderingReqDto;
import com.beyond.order_system.ordering.dto.OrderListResDto;
import com.beyond.order_system.ordering.repository.OrderDetailRepository;
import com.beyond.order_system.ordering.repository.OrderingRepository;
import com.beyond.order_system.product.domain.Product;
import com.beyond.order_system.product.repository.ProductRepository;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final OrderDetailRepository orderDetailRepository;

    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, ProductRepository productRepository, OrderDetailRepository orderDetailRepository) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public Ordering orderingCreate(List<OrderingReqDto> dtos) {



//        //        방법1.쉬운방식
////        Ordering생성 : member_id, status
//        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(()->new EntityNotFoundException("없음"));
//        Ordering ordering = orderingRepository.save(dto.toEntity(member));
//
////        OrderDetail생성 : order_id, product_id, quantity
//        for(OrderingReqDto.OrderDto orderDto : dto.getOrderDtos()){
//            Product product = productRepository.findById(orderDto.getProductId()).orElse(null);
//            int quantity = orderDto.getProductCount();
//            OrderDetail orderDetail =  OrderDetail.builder()
//                    .product(product)
//                    .quantity(quantity)
//                    .ordering(ordering)
//                    .build();
//            orderDetailRepository.save(orderDetail);
//        }
//
//        return ordering;



        //방법 2. jpa에 최적화된 방식
        //재고 개수 적용
            //내가 차감하고자 하는 개수보다 재고가 작으면 예외 발생 illegal
            //정상적으로 차감 가능하면 재고 감수 update
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("member is not found"));
        Ordering ordering = Ordering.builder()
                .member(member)
                .build();

        for(OrderingReqDto dto : dtos){
            Product product = productRepository.findById(dto.getProductId()).orElse(null);
            //재고 수 확인
            if(dto.getProductCount()>product.getStockQuantity()){
                throw new IllegalArgumentException(product.getName()+" 재고 부족");
            }
            int quantity = dto.getProductCount();
            OrderDetail orderDetail =  OrderDetail.builder()
                    .product(product)
                    .quantity(quantity)
                    .ordering(ordering) //아직 ordering을 save하지 않았는데 어떻게 ordering인지 알지? -> jpa가 알아서 해준다. cascade.persist 했기 때문
                    .build();
            ordering.getOrderDetails().add(orderDetail);

            //재고 감소
            //더티체킹으로 인해 별도의 save 불필요
            product.updateStockQunatity(dto.getProductCount());
        }

        Ordering savedOrdering = orderingRepository.save(ordering);
        return savedOrdering;
    }

    public List<OrderListResDto> orderList() {
        List<Ordering> orderingList = orderingRepository.findAll(); //주문 리스트
        List<OrderListResDto> orderListResDtoList = new ArrayList<>();
        for(Ordering ordering : orderingList){  //각 주문마다
            orderListResDtoList.add(ordering.fromEntity());
        }
        return orderListResDtoList;
    }

    public List<OrderListResDto> myOrderList() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("member not found"));
        List<Ordering> orderingList = orderingRepository.findAllByMember(member);
        List<OrderListResDto> orderListResDtoList = new ArrayList<>();
        for(Ordering ordering : orderingList){
            orderListResDtoList.add(ordering.fromEntity());
        }
        return orderListResDtoList;
    }

    public Ordering cancelOrder(Long id) {
        Ordering ordering = orderingRepository.findById(id).orElseThrow(()->new EntityNotFoundException("order not found"));
        ordering.updateStatus(OrderStatus.CANCLED);
        return ordering;
    }
}
