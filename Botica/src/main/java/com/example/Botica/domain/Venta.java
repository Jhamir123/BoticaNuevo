package com.example.Botica.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venta")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Venta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 30, nullable = false, unique = true)
  private String numeroBoleta;

  @Column(nullable = false)
  private LocalDateTime fecha;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private MetodoPago metodo;

  private String emailCliente;

  @Column(precision = 10, scale = 2, nullable = false)
  private BigDecimal subtotal;

  @Column(precision = 10, scale = 2, nullable = false)
  private BigDecimal igv;

  @Column(precision = 10, scale = 2, nullable = false)
  private BigDecimal total;

  @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<VentaDetalle> detalles = new ArrayList<>();

  public void addDetalle(VentaDetalle d) {
    d.setVenta(this);
    this.detalles.add(d);
  }
}
