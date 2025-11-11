package com.example.Botica.domain;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProductoTest {
    @Test
    void canInstantiateEntity() {
        Producto e = new Producto();
        assertThat(e).isNotNull();
    }
}
