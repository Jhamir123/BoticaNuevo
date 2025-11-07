package com.example.Botica;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    ApplicationContext ctx;

    @Test
    void passwordEncoderBeanPresent() {
        PasswordEncoder encoder = ctx.getBean(PasswordEncoder.class);
        String hashed = encoder.encode("secret");
        assertThat(encoder.matches("secret", hashed)).isTrue();
    }
}
