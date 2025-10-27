package com.example.Botica.Controller.validation;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductoDto {
    @NotBlank @Size(max=150) private String nombre;
    @NotBlank @Size(max=80) private String unidad;
    @NotNull @DecimalMin(value="0.0", inclusive=false) private BigDecimal precio;
    @NotNull @Min(0) private Integer stock;
    private boolean destacado;

    public String getNombre(){return nombre;} public void setNombre(String v){nombre=v;}
    public String getUnidad(){return unidad;} public void setUnidad(String v){unidad=v;}
    public BigDecimal getPrecio(){return precio;} public void setPrecio(BigDecimal v){precio=v;}
    public Integer getStock(){return stock;} public void setStock(Integer v){stock=v;}
    public boolean isDestacado(){return destacado;} public void setDestacado(boolean v){destacado=v;}
}
