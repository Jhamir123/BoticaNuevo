package com.example.Botica.domain;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VentaTest {
    @Test
    void canInstantiateEntity() {
        Venta e = new Venta();
        assertThat(e).isNotNull();
    }
}
