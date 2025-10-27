package com.example.Botica.Controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroDTO {
  @NotBlank @Size(min = 2, max = 80)
  private String nombres;

  @NotBlank @Size(min = 2, max = 80)
  private String apellidos;

  @NotBlank @Email @Size(max = 120)
  private String email;

  @NotBlank @Size(min = 6, max = 60)
  private String password;

  @NotBlank @Size(min = 6, max = 60)
  private String confirmarPassword;
}
