package com.example.Botica.Controller;

import com.example.Botica.Controller.dto.RegistroDTO;
import com.example.Botica.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

  private final UsuarioService usuarioService;

  @GetMapping("/login")
  public String loginPage() {
    return "login";
  }

  @GetMapping("/registro")
  public String registroForm(Model model) {
    model.addAttribute("form", new RegistroDTO());
    return "registro";
  }

  @PostMapping("/registro")
  public String registrar(@Valid @ModelAttribute("form") RegistroDTO form,
                          BindingResult br,
                          Model model) {
    if (br.hasErrors()) return "registro";
    try {
      usuarioService.registrarCliente(form);
      model.addAttribute("ok", "Tu cuenta fue creada. Ya puedes iniciar sesión.");
      return "login";
    } catch (IllegalArgumentException e) {
      model.addAttribute("error", e.getMessage());
      return "registro";
    }
  }
}
