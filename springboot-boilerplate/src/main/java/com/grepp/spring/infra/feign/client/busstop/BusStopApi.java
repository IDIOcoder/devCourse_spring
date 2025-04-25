package com.grepp.spring.infra.feign.client.busstop;

import com.grepp.spring.infra.feign.client.busstop.config.BusStopConfig;
import com.grepp.spring.infra.feign.client.busstop.dto.BusStopResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "busStopApi", url = "http://openapi.seoul.go.kr:8088"
, configuration = BusStopConfig.class)
public interface BusStopApi {
    
    @GetMapping
    BusStopResponse getBusStop(
    
    );
}
