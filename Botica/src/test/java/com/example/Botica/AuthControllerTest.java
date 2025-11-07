package com.example.Botica;

import com.example.Botica.Controller.AuthController;
import com.example.Botica.Controller.dto.RegistroDTO;
import com.example.Botica.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    @DisplayName("POST /registro: cuando la validación es correcta retorna vista login y mensaje OK")
    void registro_ok() throws Exception {
        Mockito.doNothing().when(usuarioService).registrarCliente(any(RegistroDTO.class));

        mockMvc.perform(post("/registro")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nombres", "Juan")
                        .param("apellidos", "Pérez")
                        .param("email", "juan@example.com")
                        .param("password", "Secreto123"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("ok"));
    }

    @Test
    @DisplayName("POST /registro: cuando el servicio lanza IllegalArgumentException muestra error en la vista registro")
    void registro_error_servicio() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Correo ya usado"))
                .when(usuarioService).registrarCliente(any(RegistroDTO.class));

        mockMvc.perform(post("/registro")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nombres", "Juan")
                        .param("apellidos", "Pérez")
                        .param("email", "juan@example.com")
                        .param("password", "Secreto123"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"))
                .andExpect(model().attributeExists("error"));
    }
}
