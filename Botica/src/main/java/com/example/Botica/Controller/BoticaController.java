package com.example.Botica.Controller;

import com.example.Botica.cart.CarritoItem;
import com.example.Botica.domain.Producto;
import com.example.Botica.repository.ProductoRepository;
import com.example.Botica.service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@SessionAttributes({ "carrito", "descuento", "metodoEnvio", "envio" })
public class BoticaController {

  private final CatalogoService catalogo;
  private final ProductoRepository productoRepo;

  // ---------- Inicializadores de sesión ----------
  @ModelAttribute("carrito")
  public List<CarritoItem> initCarrito() {
    return new ArrayList<>();
  }

  @ModelAttribute("descuento")
  public BigDecimal initDescuento() {
    return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
  }

  @ModelAttribute("envio")
  public BigDecimal initEnvio() {
    return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
  }

  // *** Clave para evitar IllegalStateException: siempre existirá en sesión ***
  @ModelAttribute("metodoEnvio")
  public String initMetodoEnvio() {
    // default coherente con tu lógica ("RECOJO" / "ENTREGA")
    return "RECOJO";
  }

  // ---------- Vistas públicas ----------
  @GetMapping({ "/", "/inicio" })
  public String inicio(Model model) {
    var destacados = productoRepo.findAll().stream()
        .filter(Producto::isDestacado).toList();
    model.addAttribute("destacados", destacados);
    return "inicio";
  }

  @GetMapping("/promociones")
  public String promociones(Model model) {
    model.addAttribute("items", catalogo.promociones());
    return "promociones";
  }

  @GetMapping("/catalogo")
  public String catalogo(Model model,
      @RequestParam(required = false) String categoria,
      @RequestParam(required = false) BigDecimal min,
      @RequestParam(required = false) BigDecimal max,
      @RequestParam(required = false) String q) {

    model.addAttribute("q", q);
    model.addAttribute("categoria", categoria);
    model.addAttribute("min", min);
    model.addAttribute("max", max);

    model.addAttribute("items",
        (q != null && !q.isBlank()) ? catalogo.buscar(q)
            : catalogo.catalogo(categoria, min, max));
    return "catalogo";
  }

  // ---------- Carrito ----------
  @PostMapping("/carrito/agregar/{id}")
  public String agregar(@PathVariable Long id,
      @RequestParam(defaultValue = "1") int cantidad,
      @ModelAttribute("carrito") List<CarritoItem> carrito) {

    // asegura mínimo 1
    cantidad = Math.max(1, cantidad);

    var prod = productoRepo.findById(id).orElseThrow();
    var existente = carrito.stream()
        .filter(ci -> ci.getProducto().getId().equals(id)).findFirst();

    if (existente.isPresent()) {
      existente.get().setCantidad(existente.get().getCantidad() + cantidad);
    } else {
      carrito.add(CarritoItem.builder().producto(prod).cantidad(cantidad).build());
    }
    return "redirect:/carrito";
  }

  @GetMapping("/carrito")
  public String verCarrito(@ModelAttribute("carrito") List<CarritoItem> carrito,
      @ModelAttribute("descuento") BigDecimal descuento,
      @ModelAttribute("metodoEnvio") String metodoEnvio,
      Model model) {

    // tolerancia por si algo llega nulo
    if (carrito == null) carrito = new ArrayList<>();
    if (descuento == null) descuento = BigDecimal.ZERO;
    if (metodoEnvio == null || metodoEnvio.isBlank()) metodoEnvio = "RECOJO";

    Totales t = calcularTotales(carrito, descuento, metodoEnvio);

    model.addAttribute("subtotal", t.subtotal);
    model.addAttribute("descuento", t.descuento);
    model.addAttribute("envio", t.envio);
    model.addAttribute("igv", t.igv);
    model.addAttribute("total", t.total);

    return "carrito";
  }

  @PostMapping("/carrito/cambiar/{id}")
  public String cambiarCantidad(@PathVariable Long id,
      @RequestParam int cantidad,
      @ModelAttribute("carrito") List<CarritoItem> carrito) {

    carrito.stream()
        .filter(ci -> ci.getProducto().getId().equals(id))
        .findFirst()
        .ifPresent(ci -> ci.setCantidad(Math.max(1, cantidad)));
    return "redirect:/carrito";
  }

  @PostMapping("/carrito/eliminar/{id}")
  public String eliminar(@PathVariable Long id,
      @ModelAttribute("carrito") List<CarritoItem> carrito) {

    carrito.removeIf(ci -> ci.getProducto().getId().equals(id));
    return "redirect:/carrito";
  }

  @PostMapping("/carrito/cupon")
  public String aplicarCupon(@RequestParam String codigo,
      @ModelAttribute("carrito") List<CarritoItem> carrito,
      Model model) {

    BigDecimal descuento = BigDecimal.ZERO;
    BigDecimal subtotal = (carrito == null ? new ArrayList<CarritoItem>() : carrito).stream()
        .map(CarritoItem::getImporte)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    if ("SALUD10".equalsIgnoreCase(codigo)) {
      descuento = subtotal.multiply(new BigDecimal("0.10"));
    }
    model.addAttribute("descuento", descuento.setScale(2, RoundingMode.HALF_UP));
    return "redirect:/carrito";
  }

  @PostMapping("/carrito/envio")
  public String cambiarMetodoEnvio(@RequestParam("metodo") String metodo, Model model) {
    model.addAttribute("metodoEnvio",
        ("RECOJO".equalsIgnoreCase(metodo)) ? "RECOJO" : "ENTREGA");
    return "redirect:/carrito";
  }

  // ---------- Cálculo de totales ----------
  private Totales calcularTotales(List<CarritoItem> carrito, BigDecimal desc, String metodoEnvio) {
    BigDecimal subtotal = (carrito == null ? new ArrayList<CarritoItem>() : carrito).stream()
        .map(CarritoItem::getImporte)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);

    BigDecimal descuento = (desc == null ? BigDecimal.ZERO : desc).setScale(2, RoundingMode.HALF_UP);
    BigDecimal base = subtotal.subtract(descuento);
    if (base.signum() < 0) base = BigDecimal.ZERO;

    BigDecimal envio = ("RECOJO".equalsIgnoreCase(metodoEnvio))
        ? BigDecimal.ZERO
        : (base.compareTo(new BigDecimal("150.00")) >= 0
            ? BigDecimal.ZERO
            : new BigDecimal("9.90"));
    envio = envio.setScale(2, RoundingMode.HALF_UP);

    BigDecimal igv = base.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
    BigDecimal total = base.add(envio).add(igv).setScale(2, RoundingMode.HALF_UP);

    return new Totales(subtotal, descuento, envio, igv, total);
  }

  private static class Totales {
    final BigDecimal subtotal, descuento, envio, igv, total;
    Totales(BigDecimal s, BigDecimal d, BigDecimal e, BigDecimal i, BigDecimal t) {
      subtotal = s;
      descuento = d;
      envio = e;
      igv = i;
      total = t;
    }
  }
}
