package com.example.Botica.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

@SpringBootTest
class SecurityConfigTest {
    @Autowired
    private SecurityConfig bean;

    @Test
    void bean_is_loaded_in_context() {
        assertThat(bean).isNotNull();
    }
}
