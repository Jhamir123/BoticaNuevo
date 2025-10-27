package com.example.Botica.Controller;

import com.example.Botica.cart.CarritoItem;
import com.example.Botica.domain.MetodoPago;
import com.example.Botica.domain.Venta;
import com.example.Botica.repository.VentaRepository;
import com.example.Botica.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@SessionAttributes({"carrito", "descuento", "metodoEnvio"})
public class CheckoutController {

  private final CheckoutService checkoutService;
  private final VentaRepository ventaRepo;

  // ===== Atributos de sesión =====
  @ModelAttribute("carrito")
  public List<CarritoItem> initCarrito() { return new ArrayList<>(); }

  @ModelAttribute("descuento")
  public BigDecimal initDescuento() { return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP); }

  @ModelAttribute("metodoEnvio")
  public String initMetodoEnvio() { return "ENTREGA"; }

  // ===== Página de pago =====
  @GetMapping("/pago")
  public String pago(@ModelAttribute("carrito") List<CarritoItem> carrito,
                     @ModelAttribute("descuento") BigDecimal descuento,
                     @ModelAttribute("metodoEnvio") String metodoEnvio,
                     Model model) {

    BigDecimal subtotal = carrito.stream()
        .map(CarritoItem::getImporte)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);

    BigDecimal dsc = (descuento == null ? BigDecimal.ZERO : descuento)
        .setScale(2, RoundingMode.HALF_UP);

    BigDecimal base = subtotal.subtract(dsc);
    if (base.signum() < 0) base = BigDecimal.ZERO;

    BigDecimal envio;
    if ("RECOJO".equalsIgnoreCase(metodoEnvio)) {
      envio = BigDecimal.ZERO;
    } else {
      envio = base.compareTo(new BigDecimal("150.00")) >= 0
              ? BigDecimal.ZERO
              : new BigDecimal("9.90");
    }
    envio = envio.setScale(2, RoundingMode.HALF_UP);

    BigDecimal igv = base.multiply(new BigDecimal("0.18"))
        .setScale(2, RoundingMode.HALF_UP);

    BigDecimal total = base.add(envio).add(igv)
        .setScale(2, RoundingMode.HALF_UP);

    model.addAttribute("subtotal", subtotal);
    model.addAttribute("descuento", dsc);
    model.addAttribute("envio", envio);
    model.addAttribute("igv", igv);
    model.addAttribute("total", total);

    return "pago";
  }

  // ===== Confirmar pago =====
  @PostMapping("/pago/confirmar")
  public String confirmar(@RequestParam(name = "metodoPago", required = false) String metodoStr,
                          @ModelAttribute("carrito") List<CarritoItem> carrito,
                          RedirectAttributes ra) {

    if (metodoStr == null || metodoStr.isBlank()) metodoStr = "YAPE";
    MetodoPago metodo = MetodoPago.valueOf(metodoStr.toUpperCase());

    String emailCliente = "cliente@demo.com"; // Simulación

    Venta venta = checkoutService.confirmarCompra(metodo, carrito, emailCliente);

    carrito.clear();

    ra.addFlashAttribute("ok", "¡Pago registrado! N° " + venta.getNumeroBoleta());
    return "redirect:/comprobante/" + venta.getId();
  }

  // ===== Comprobante =====
  @GetMapping("/comprobante/{id}")
  public String comprobante(@PathVariable Long id, Model model) {
    Venta venta = ventaRepo.findWithDetallesById(id)
        .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

    model.addAttribute("venta", venta);
    model.addAttribute("detalles", venta.getDetalles());
    model.addAttribute("subtotal", venta.getSubtotal());
    model.addAttribute("igv", venta.getIgv());
    model.addAttribute("total", venta.getTotal());
    return "comprobante";
  }

  // ===== PDF Placeholder =====
  @GetMapping("/comprobante/{id}/pdf")
  public String descargarPdf(@PathVariable Long id, Model model) {
    Venta venta = ventaRepo.findWithDetallesById(id)
        .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
    model.addAttribute("venta", venta);
    model.addAttribute("detalles", venta.getDetalles());
    return "comprobante";
  }
}
