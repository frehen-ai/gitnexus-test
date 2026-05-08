package com.example.frehen.testgitnexus.controller.hello;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloControllerTest {

    private final HelloController controller = new HelloController();

    @Test
    void sayHello_returnsFormattedMessage() {
        var request = new HelloController.HelloRequest("Alice", "welcome aboard");
        var response = controller.sayHello(request);
        assertEquals("Hello Alice, welcome aboard", response.message());
    }
}
