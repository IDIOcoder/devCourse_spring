package com.grepp.spring.infra.feign.client.busstop;


import static org.assertj.core.api.Assertions.*;

import com.grepp.spring.infra.feign.error.FeignCommonException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BusStopApiTest {

    @Autowired
    BusStopApi busStopApi;
    
    @Value("${bus-stop.apikey}")
    String apiKey;
    
    @Test
    public void feignTest(){
        System.out.println(busStopApi.getBusStop(apiKey,
            1,5));
    }
    
    @Test
    public void getBusStop_throwEx_invalidApiKey(){
       assertThatThrownBy(() -> busStopApi.getBusStop("1111",1,1000))
           .isInstanceOf(FeignCommonException.class)
           .satisfies(ex -> {
               System.out.println(ex);
           });
    }

}