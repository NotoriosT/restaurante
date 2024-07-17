package com.tupa.restaurante.entidades;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdutoPedido {
    private Produto produto;
    private int quantidade;
    private double total;
    private String observacao;
}
