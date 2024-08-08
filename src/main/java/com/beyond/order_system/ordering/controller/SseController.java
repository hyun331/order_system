package com.beyond.order_system.ordering.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SseController {
    //SseEmitter : 연결된 사용자 정보를 의미
    //ConcurrentHashMap : Thread-safe한 map = 멀티 스레드 상황에서 안전 => 동시성 이슈 발생 안함
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();


    //연결이 들어올 수 있도록 api 생성
    @GetMapping("/subscribe")
    public SseEmitter subscribe(){
        SseEmitter emitter = new SseEmitter(14400*60*1000L); // 정도로 emitter유효시간 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        emitters.put(email, emitter);   //사용자에 대한 정보가 emiiters에 저장되어 있어야 사용자는 알림을 받을 수 있다.
        emitter.onCompletion(()->emitters.remove(email));   //할거 다하면 emitters에서 제거
        emitter.onTimeout(()->emitters.remove(email));      //시간 지나면 emitters에서 제거

        try{
//            연결을 요청한 emitter에게 연결되었다고 보내기
            emitter.send(SseEmitter.event().name("connect").data("connected!!!!"));
        }catch(IOException e){
            e.printStackTrace();
        }

        return emitter;
    }
}
