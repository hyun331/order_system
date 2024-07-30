package com.beyond.order_system.ordering.service;

import com.beyond.order_system.common.config.RabbitMqConfig;
import com.beyond.order_system.ordering.dto.StockDecreaseEvent;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class StockDecreaseEventHandler {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void publish(StockDecreaseEvent event){
        //rabbitTemplate.convertAndSend(큐 이름 , 객체);
        rabbitTemplate.convertAndSend(RabbitMqConfig.STOCK_DECREASE_QUEUE, event);
    }

    //Component 어노테이션 있으면 Transactional 붙일 수 있음. 에러 발생시 롤백 처리할거임
//    @Transactional
//    //orderingService에서 mq에 발행한 것들을 바라보고있어야함
//    @RabbitListener(queues = RabbitMqConfig.STOCK_DECREASE_QUEUE)
//    public void listen(){
//
//    }
}
