package com.example.Botica;

import com.example.Botica.domain.Producto;
import com.example.Botica.repository.ProductoRepository;
import com.example.Botica.service.CatalogoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CatalogoServiceTest {

    private ProductoRepository repo;
    private CatalogoService service;

    @BeforeEach
    void setUp() {
        repo = mock(ProductoRepository.class);
        service = new CatalogoService(repo);
    }

    @Test
    @DisplayName("catalogo: normaliza nulls y delega al repo con valores por defecto")
    void catalogo_nulls_are_normalized() {
        // when
        service.catalogo(null, null, null);

        // then
        ArgumentCaptor<String> cat = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BigDecimal> min = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<BigDecimal> max = ArgumentCaptor.forClass(BigDecimal.class);

        verify(repo).findByCategoriaIgnoreCaseContainingAndPrecioBetweenOrderByNombreAsc(
                cat.capture(), min.capture(), max.capture());

        assertThat(cat.getValue()).isEqualTo("");
        assertThat(min.getValue()).isEqualByComparingTo("0");
        assertThat(max.getValue()).isEqualByComparingTo("9999");
    }

    @Test
    @DisplayName("buscar: convierte q=null en cadena vacía")
    void buscar_null_to_empty() {
        when(repo.findByNombreIgnoreCaseContainingOrderByNombreAsc(""))
                .thenReturn(List.of());

        assertThat(service.buscar(null)).isEmpty();

        verify(repo).findByNombreIgnoreCaseContainingOrderByNombreAsc("");
    }

    @Test
    @DisplayName("listarDestacados: filtra por flag isDestacado() sobre findAll()")
    void listar_destacados() {
        Producto a = Producto.builder().nombre("A").destacado(true).build();
        Producto b = Producto.builder().nombre("B").destacado(false).build();
        when(repo.findAll()).thenReturn(List.of(a, b));

        List<Producto> destacados = service.listarDestacados();

        assertThat(destacados).containsExactly(a);
        verify(repo, times(1)).findAll();
    }
}
