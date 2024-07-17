package com.tupa.restaurante.entidades;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "produtos")
public class Produto {
    @Id
    private String id;
    private String nome;
    private double preco;
    private String tipo; // Tipo do produto: CHURRASCO, BEBIDA, COZINHA, etc.
    private String imagem; // Campo para armazenar a imagem em Base64
}
