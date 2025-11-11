package com.example.Botica.domain;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UsuarioTest {
    @Test
    void canInstantiateEntity() {
        Usuario e = new Usuario();
        assertThat(e).isNotNull();
    }
}
