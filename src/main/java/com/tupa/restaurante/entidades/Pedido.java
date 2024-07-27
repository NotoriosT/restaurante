package com.tupa.restaurante.entidades;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Document(collection = "pedidos")
public class Pedido {
    @Id
    private String id;

    @NotNull(message = "O ID da mesa é obrigatório")
    private int mesaId;

    @NotEmpty(message = "A lista de produtos não pode estar vazia")
    private List<ProdutoPedido> produtos;

    private String observacao;

    @NotNull(message = "O status é obrigatório")
    private Status status;
}
