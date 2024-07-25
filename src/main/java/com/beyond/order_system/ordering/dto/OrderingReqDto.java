package com.beyond.order_system.ordering.dto;

import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.ordering.domain.OrderDetail;
import com.beyond.order_system.ordering.domain.OrderStatus;
import com.beyond.order_system.ordering.domain.Ordering;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderingReqDto {
    private Long memberId;
    private List<OrderDetailDto> orderDetails;
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderDetailDto{
        private Long productId;
        private Integer productCount;
    }


}
