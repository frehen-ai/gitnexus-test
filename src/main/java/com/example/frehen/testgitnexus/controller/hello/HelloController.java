package com.example.frehen.testgitnexus.controller.hello;

import com.example.frehen.testgitnexus.service.HelloService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    public record HelloRequest(String user, String message) {}

    public record HelloResponse(String message) {}

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @PostMapping("/hello")
    public HelloResponse sayHello(@RequestBody HelloRequest request) {
        return helloService.sayHello(request.user(), request.message());
    }
}
