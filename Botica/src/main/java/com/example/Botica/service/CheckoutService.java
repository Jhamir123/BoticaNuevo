package com.example.Botica.service;

import com.example.Botica.cart.CarritoItem;
import com.example.Botica.domain.MetodoPago;
import com.example.Botica.domain.Venta;
import com.example.Botica.domain.VentaDetalle;
import com.example.Botica.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CheckoutService {

  private final VentaRepository ventaRepo;

  public Venta confirmarCompra(MetodoPago metodo, List<CarritoItem> carrito, String emailCliente) {
    if (carrito == null || carrito.isEmpty()) {
      throw new IllegalArgumentException("El carrito está vacío.");
    }

    BigDecimal subtotal = carrito.stream()
        .map(CarritoItem::getImporte)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal igv = subtotal.multiply(new BigDecimal("0.18"))
                             .setScale(2, RoundingMode.HALF_UP);

    BigDecimal total = subtotal.add(igv);

    Venta venta = Venta.builder()
        .numeroBoleta(generarNumeroBoleta())
        .fecha(LocalDateTime.now())
        .metodo(metodo)
        .emailCliente(emailCliente)
        .subtotal(subtotal)
        .igv(igv)
        .total(total)
        .build();

    carrito.forEach(item -> {
      var d = VentaDetalle.builder()
          .venta(venta)
          .producto(item.getProducto())
          .cantidad(item.getCantidad())
          .precioUnitario(item.getProducto().getPrecio())
          .subtotal(item.getImporte())
          .build();
      venta.addDetalle(d);
    });

    return ventaRepo.save(venta);
  }

  private String generarNumeroBoleta() {
    String base = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    String suf = String.format("%04d", new Random().nextInt(10000));
    String nro = "BOT" + base + "-" + suf;
    if (ventaRepo.existsByNumeroBoleta(nro)) return generarNumeroBoleta();
    return nro;
  }
}
