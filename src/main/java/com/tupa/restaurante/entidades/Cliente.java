package com.tupa.restaurante.entidades;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "clientes")
public class Cliente {
    @Id
    private String id;
    private String nome;
    private String telefone;
    private String endereco;
    private String email;
    private String cpf;
}
