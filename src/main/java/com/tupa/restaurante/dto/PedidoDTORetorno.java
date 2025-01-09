package com.tupa.restaurante.dto;

import com.tupa.restaurante.entidades.CancelamentoProdutoPedido;
import com.tupa.restaurante.entidades.ProdutoPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal; // Import necessário
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PedidoDTORetorno {

    private String idPedido;

    private List<ProdutoPedidoDTO> produtos;

    private int numeroMesa;

    private String idConta;

    private String observacao;

    private String status;
    private String idFechamento;
    private String nomeCliente;
    private LocalDateTime dataPedido;
    private String idMesa;
    private List<CancelamentoProdutoPedido> cancelamentoProdutoPedidosList;

    // Novo campo para o total do pedido
    private BigDecimal total;
}
