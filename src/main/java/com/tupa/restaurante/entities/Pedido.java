package com.tupa.restaurante.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tupa.restaurante.entities.enums.Status;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataPedido;

    // Lista para armazenar cancelamentos no nível do pedido
    private List<CancelamentoProdutoPedido> cancelamentos = new ArrayList<>();
}
