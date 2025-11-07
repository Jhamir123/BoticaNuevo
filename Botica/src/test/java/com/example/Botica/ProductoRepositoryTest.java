package com.example.Botica;

import com.example.Botica.domain.Producto;
import com.example.Botica.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repo;

    @BeforeEach
    void seed() {
        repo.deleteAll();

        repo.save(Producto.builder()
                .nombre("Ibuprofeno 400mg")
                .categoria("Analgésicos")
                .precio(new BigDecimal("8.20"))
                .promoActiva(true)
                .destacado(true)
                .build());

        repo.save(Producto.builder()
                .nombre("Paracetamol 500mg")
                .categoria("Analgésicos")
                .precio(new BigDecimal("3.50"))
                .promoActiva(false)
                .destacado(false)
                .build());

        repo.save(Producto.builder()
                .nombre("Vitamina C 1g")
                .categoria("Vitaminas")
                .precio(new BigDecimal("1.20"))
                .promoActiva(true)
                .destacado(false)
                .build());
    }

    @Test
    @DisplayName("findByPromoActivaTrueOrderByNombreAsc: solo promocoes activas ordenadas por nombre")
    void promoActivasOrden() {
        List<Producto> list = repo.findByPromoActivaTrueOrderByNombreAsc();
        assertThat(list).extracting(Producto::getNombre)
                .containsExactly("Ibuprofeno 400mg", "Vitamina C 1g");
    }

    @Test
    @DisplayName("findByCategoria...AndPrecioBetween: filtra por categoria (case-insensitive, contains) y rango de precio")
    void categoriaYRango() {
        List<Producto> list = repo.findByCategoriaIgnoreCaseContainingAndPrecioBetweenOrderByNombreAsc(
                "analg", new BigDecimal("3.00"), new BigDecimal("10.00"));

        assertThat(list).extracting(Producto::getNombre)
                .containsExactly("Ibuprofeno 400mg", "Paracetamol 500mg");
    }

    @Test
    @DisplayName("findByNombreIgnoreCaseContainingOrderByNombreAsc: búsqueda por nombre sin sensibilidad a mayúsculas")
    void buscarPorNombre() {
        List<Producto> list = repo.findByNombreIgnoreCaseContainingOrderByNombreAsc("vitamina");
        assertThat(list).extracting(Producto::getCategoria).containsExactly("Vitaminas");
    }
}
