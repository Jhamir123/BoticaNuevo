package com.example.Botica;

import com.example.Botica.Controller.validation.ProductoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProductoDtoValidationTest {

    private final Validator validator;

    public ProductoDtoValidationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("ProductoDto: valida campos obligatorios y rangos (nombre/unidad no blank, precio > 0, stock >= 0)")
    void dto_validation() {
      
        ProductoDto invalido = new ProductoDto();
        invalido.setNombre("");                   
        invalido.setUnidad(null);                  
        invalido.setPrecio(new BigDecimal("-1"));   
        invalido.setStock(null);                    

        Set<ConstraintViolation<ProductoDto>> v1 = validator.validate(invalido);
        assertThat(v1).isNotEmpty();

        
        ProductoDto valido = new ProductoDto();
        valido.setNombre("Aspirina 100mg");
        valido.setUnidad("Tabletas");
        valido.setPrecio(new BigDecimal("2.50"));
        valido.setStock(10);
        valido.setDestacado(true); 

        Set<ConstraintViolation<ProductoDto>> v2 = validator.validate(valido);
        assertThat(v2).isEmpty();
    }
} 
