package com.tupa.restaurante.dto;

import com.tupa.restaurante.entidades.ProdutoPedido;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PedidoDTORetorno {

    private String idPedido;

    private List<ProdutoPedido> produtos;

    private int numeroMesa;

    private String idConta;

    private String observacao;

    private String status;
    private  String idFechamento;
}
