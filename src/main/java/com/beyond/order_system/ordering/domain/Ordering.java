package com.beyond.order_system.ordering.domain;

import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.ordering.dto.OrderingResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ordering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "ordering", cascade = CascadeType.PERSIST)
    @Builder.Default    //빌더패터을 사용하면서
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public OrderingResDto fromEntity(){
        return OrderingResDto.builder()
                .id(this.id)
                .memberId(this.member.getId())
                .orderDetails(this.orderDetails)
                .build();
    }
}
