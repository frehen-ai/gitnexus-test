package com.example.frehen.testgitnexus.controller.message;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc
@Import(MessageControllerIntegrationTest.FixedClockConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MessageControllerIntegrationTest {

    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        @Primary
        public Clock clock() {
            return Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getMessages_returnsPostedMessages() throws Exception {
        // Post two messages
        mockMvc.perform(post("/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"user": "Alice", "message": "hello world"}
                        """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"user": "Bob", "message": "hi there"}
                        """))
                .andExpect(status().isOk());

        // Retrieve all messages in interval
        mockMvc.perform(get("/messages")
                        .param("from", "2024-01-01T11:00:00Z")
                        .param("to", "2024-01-01T13:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].user").value("Alice"))
                .andExpect(jsonPath("$[1].user").value("Bob"));
    }

    @Test
    void getMessages_filteredByUsers() throws Exception {
        mockMvc.perform(post("/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"user": "Alice", "message": "msg1"}
                        """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"user": "Bob", "message": "msg2"}
                        """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"user": "Charlie", "message": "msg3"}
                        """))
                .andExpect(status().isOk());

        // Filter to only Alice and Charlie
        mockMvc.perform(get("/messages")
                        .param("from", "2024-01-01T11:00:00Z")
                        .param("to", "2024-01-01T13:00:00Z")
                        .param("users", "Alice", "Charlie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].user").value("Alice"))
                .andExpect(jsonPath("$[1].user").value("Charlie"));
    }

    @Test
    void getMessages_emptyInterval_returnsEmpty() throws Exception {
        mockMvc.perform(post("/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"user": "Alice", "message": "msg"}
                        """))
                .andExpect(status().isOk());

        // Query outside the fixed clock time
        mockMvc.perform(get("/messages")
                        .param("from", "2024-01-02T00:00:00Z")
                        .param("to", "2024-01-02T23:59:59Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
