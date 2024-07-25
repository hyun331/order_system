package com.beyond.order_system.ordering.dto;

import com.beyond.order_system.ordering.domain.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderingResDto {
    private Long id;
    private Long memberId;
    private List<OrderDetail> orderDetails;
}
