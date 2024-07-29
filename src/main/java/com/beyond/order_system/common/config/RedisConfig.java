package com.beyond.order_system.common.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    //@Value 어노테이션을 통해 yml파일에 있는 정보 가져옴
    @Value("${spring.redis.host}")
    public String host;

    @Value("${spring.redis.port}")
    public int port;

    @Bean
    @Qualifier("2") //1번 db를 쓰는데 왜 2? -> db는 0부터 시작, Qualifier는 1부터 시작
    //RedisConnectionFactory는 Redis 서버와의 연결을 설정하는 역할
    //LettuceConnectionFactory는 RedisConnectionFactory의 구현체로서 실질적인 역할 수행
    public RedisConnectionFactory redisConnectionFactory(){
        //아래 줄처럼 한줄로 host, port만 주거나
//        return new LettuceConnectionFactory(host, port);

        //이렇게 설정할 수도 있음
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(1);   //board랑 겹칠까봐 1번 db 사용
        return new LettuceConnectionFactory(configuration);
    }


    //redisTemplate은 redis와 상호작용할 때 redis key, value의 형식을 정의.
    //key는 string, value는 object
    @Bean
    @Qualifier("2") //1번 db를 쓰는데 왜 2? -> db는 0부터 시작, Qualifier는 1부터 시작
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("2") RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    //redisTemplate.opsForValue().set(key, value)
    //redisTemplate.opsForValue().get(key)
    //redisTemplate opsForValue().increment 또는 decrement
}
