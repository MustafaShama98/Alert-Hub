package org.example.securityservice.feign;


import org.example.securityservice.dto.UserRequest;
import org.example.securityservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "users-service", path = "/api/users/")
public interface UsersFeignClient {
    // Define methods to interact with the users-service API

    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable Long id);

    @PostMapping
    UserResponse createUser(@RequestBody UserRequest user);


}
