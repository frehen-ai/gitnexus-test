package com.example.frehen.testgitnexus.controller.message;

import com.example.frehen.testgitnexus.repository.Message;
import com.example.frehen.testgitnexus.repository.MessageRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
public class MessageController {

    private final MessageRepository messageRepository;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("/messages")
    public List<Message> getMessages(
            @RequestParam Instant from,
            @RequestParam Instant to,
            @RequestParam(required = false) List<String> users) {

        if (users != null && !users.isEmpty()) {
            return messageRepository.findByIntervalAndUsers(from, to, users);
        }
        return messageRepository.findByInterval(from, to);
    }
}
