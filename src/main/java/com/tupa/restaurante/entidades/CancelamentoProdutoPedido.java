package com.tupa.restaurante.entidades;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CancelamentoProdutoPedido {
    private String idPedido; // ID do pedido associado ao cancelamento
    private LocalDateTime dataCancelamento;
    private String canceladoPor; // Pode ser o ID do usuário ou nome
    private String idProduto; // ID do produto cancelado
    private int quantidadeCancelada;
    private String motivo; // Opcional: motivo do cancelamento
}
