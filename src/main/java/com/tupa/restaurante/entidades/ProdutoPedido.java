package com.tupa.restaurante.entidades;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProdutoPedido {

    @NotNull(message = "O ID do produto é obrigatório")
    private String idProduto;

    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    private int quantidade;

    private BigDecimal total;

    private String observacao;


}
