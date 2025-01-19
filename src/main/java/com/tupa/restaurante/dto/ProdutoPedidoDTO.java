package com.tupa.restaurante.dto;

import com.tupa.restaurante.entities.CancelamentoProdutoPedido;
import com.tupa.restaurante.entities.Produto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class ProdutoPedidoDTO  {

    private Produto Produto;

    private int quantidade;

    private BigDecimal total;

    private String observacao;

    // Nova lista para armazenar os cancelamentos
    private List<CancelamentoProdutoPedido> cancelamentos = new ArrayList<>();
}
