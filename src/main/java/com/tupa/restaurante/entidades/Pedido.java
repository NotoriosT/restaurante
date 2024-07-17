package com.tupa.restaurante.entidades;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Getter
@Setter
@Document(collection = "pedidos")
public class Pedido {
    @Id
    private String id;
    private int mesaId;
    private List<ProdutoPedido> produtos;
    private String observacao;
    private String status;
}
