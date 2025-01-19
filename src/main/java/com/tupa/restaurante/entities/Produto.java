package com.tupa.restaurante.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter
@Setter
@Document(collection = "Produtos")
public class Produto {
    @Id
    private String id;
    private String nome;
    private BigDecimal preco;
    private String tipo; // Tipo do produto: CHURRASCO, BEBIDA, COZINHA, etc.
    private String imagem; // Campo para armazenar a imagem em Base64
}
