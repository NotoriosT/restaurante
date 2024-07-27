package com.tupa.restaurante.entidades;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class ProdutoPedido {

    @NotNull(message = "O produto é obrigatório")
    private Produto produto;

    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    private int quantidade;

    private double total;

    private String observacao;
}