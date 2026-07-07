package com.ecommerce.order_service.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.order_service.dto.UserResponseDTO;

@FeignClient(
        name="user-service",
        url="http://user-service:8081"
)
public interface UserClient {
    @GetMapping("/api/users/internal/{id}")
    UserResponseDTO getUser(
            @PathVariable Long id
    );
}