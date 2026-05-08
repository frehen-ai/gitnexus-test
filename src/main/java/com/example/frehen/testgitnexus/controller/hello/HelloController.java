package com.example.frehen.testgitnexus.controller.hello;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    public record HelloRequest(String name, String message) {}

    public record HelloResponse(String message) {}

    @PostMapping("/hello")
    public HelloResponse sayHello(@RequestBody HelloRequest request) {
        return new HelloResponse("Hello " + request.name() + ", " + request.message());
    }
}
