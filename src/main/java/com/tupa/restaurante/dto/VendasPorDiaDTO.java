// src/main/java/com/tupa/restaurante/dto/VendasPorDiaDTO.java
package com.tupa.restaurante.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class VendasPorDiaDTO {
    private LocalDate data;
    private BigDecimal totalVendas;

    // Construtores, Getters e Setters

    public VendasPorDiaDTO() {
    }

    public VendasPorDiaDTO(LocalDate data, BigDecimal totalVendas) {
        this.data = data;
        this.totalVendas = totalVendas;
    }

}
