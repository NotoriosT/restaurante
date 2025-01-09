package com.tupa.restaurante.entidades;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "contas")
public class Conta {
    @Id
    private String id;

    private String idMesa;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private StatusConta status;// ABERTA, FECHADA
    private String idCliente;
    private String idFechamento;

    // Opcional: armazenar total se desejar persistir após fechamento
    private BigDecimal total;
}
