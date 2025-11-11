package com.example.Botica.domain;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VentaDetalleTest {
    @Test
    void canInstantiateEntity() {
        VentaDetalle e = new VentaDetalle();
        assertThat(e).isNotNull();
    }
}
