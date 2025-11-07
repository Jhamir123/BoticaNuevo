package com.example.Botica;

import com.example.Botica.Controller.AuthController;
import com.example.Botica.Controller.dto.RegistroDTO;
import com.example.Botica.domain.Usuario;
import com.example.Botica.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de AuthController: /registro
 * Se desactiva seguridad para evitar 401 en WebMvcTest.
 */
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // 🔑 DESACTIVA LOS FILTROS DE SECURITY
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    @DisplayName("POST /registro: cuando la validación es correcta retorna vista login y mensaje OK")
    void registro_ok() throws Exception {
        // Simula retorno exitoso del servicio
        Usuario dummy = Usuario.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .email("juan@example.com")
                .passwordHash("hash")
                .enabled(true)
                .role("ROLE_CLIENTE")
                .build();

        when(usuarioService.registrarCliente(any(RegistroDTO.class))).thenReturn(dummy);

        mockMvc.perform(post("/registro")
                        .with(csrf()) // aunque los filtros están desactivados, lo mantenemos por claridad
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nombres", "Juan")
                        .param("apellidos", "Perez")
                        .param("email", "juan@example.com")
                        .param("password", "Secreto123")
                        .param("confirmarPassword", "Secreto123"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("ok"));
    }

    @Test
    @DisplayName("POST /registro: cuando el servicio lanza IllegalArgumentException muestra error en la vista registro")
    void registro_error_servicio() throws Exception {
        // Simula excepción lanzada por el servicio
        when(usuarioService.registrarCliente(any(RegistroDTO.class)))
                .thenThrow(new IllegalArgumentException("Correo ya usado"));

        mockMvc.perform(post("/registro")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nombres", "Maria")
                        .param("apellidos", "Lopez")
                        .param("email", "maria@example.com")
                        .param("password", "Clave123")
                        .param("confirmarPassword", "Clave123"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"))
                .andExpect(model().attributeExists("error"));
    }
}
