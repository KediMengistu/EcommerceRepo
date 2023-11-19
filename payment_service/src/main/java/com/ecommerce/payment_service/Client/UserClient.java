package com.ecommerce.payment_service.Client;

import com.ecommerce.payment_service.OtherServiceObjects.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${application.config.user-url}")
public interface UserClient {

    @GetMapping("/username_getuser")
    User findPayerFromUsername(@RequestParam String username);
}
