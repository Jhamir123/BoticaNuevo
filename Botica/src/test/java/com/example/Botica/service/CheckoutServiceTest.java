package com.example.Botica.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SpringBootTest
class CheckoutServiceTest {
    @Autowired
    private CheckoutService service;

    @Test
    void service_is_loaded_in_context() {
        assertThat(service).isNotNull();
    }
}
