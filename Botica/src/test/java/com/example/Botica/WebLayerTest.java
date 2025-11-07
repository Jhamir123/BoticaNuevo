package com.example.Botica;

import com.example.Botica.Controller.BoticaController;
import com.example.Botica.repository.ProductoRepository;
import com.example.Botica.service.CatalogoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = BoticaController.class)
class WebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogoService catalogoService;

    @MockBean
    private ProductoRepository productoRepository;

    @Test
    @WithMockUser
    @DisplayName("GET /inicio responde 200 y renderiza la vista 'inicio'")
    void inicioOk() throws Exception {
      
        when(catalogoService.listarDestacados()).thenReturn(List.of());

        mockMvc.perform(get("/inicio").with(csrf())) 
                .andExpect(status().isOk())
                .andExpect(view().name("inicio"));
    }
}
