package com.tupa.restaurante.entidades;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "pedidos")
public class Pedido {
    @Id
    private String id;

    @NotEmpty(message = "A lista de produtos não pode estar vazia")
    private List<ProdutoPedido> produtos;

    @NotEmpty(message = "O ID da conta é obrigatório")
    private String idConta;

    private String observacao;

    @NotNull(message = "O status é obrigatório")
    private Status status;
    private LocalDateTime dataPedido;
}
