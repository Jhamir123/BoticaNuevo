package com.example.Botica.service;

import com.example.Botica.Controller.dto.RegistroDTO;
import com.example.Botica.domain.Usuario;
import com.example.Botica.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public Usuario registrarCliente(RegistroDTO dto) {
    if (usuarioRepository.existsByEmail(dto.getEmail())) {
      throw new IllegalArgumentException("El correo ya está registrado");
    }
    if (!dto.getPassword().equals(dto.getConfirmarPassword())) {
      throw new IllegalArgumentException("Las contraseñas no coinciden");
    }

    Usuario u = Usuario.builder()
        .nombres(dto.getNombres().trim())
        .apellidos(dto.getApellidos().trim())
        .email(dto.getEmail().trim().toLowerCase())
        .passwordHash(passwordEncoder.encode(dto.getPassword()))
        .enabled(true)
        .role("ROLE_CLIENTE")
        .build();
    return usuarioRepository.save(u);
  }
}
