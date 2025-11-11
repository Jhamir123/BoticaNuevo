package com.example.Botica.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

@WebMvcTest(ExportController.class)
class ExportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void exportcontroller_respondsOK_on_known_endpoint() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk());
    }
}
