package com.ecommerce.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "order-service")
public interface OrderClient {

}