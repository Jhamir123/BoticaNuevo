package com.example.Botica.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
  @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 80)
  private String nombres;

  @Column(nullable = false, length = 80)
  private String apellidos;

  @Column(nullable = false, length = 120)
  private String email;

  @Column(nullable = false, length = 120)
  private String passwordHash;

  @Column(nullable = false)
  private boolean enabled;

  @Column(nullable = false, length = 20)
  private String role;
}
