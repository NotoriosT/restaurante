package com.tupa.restaurante.entidades;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Getter
@Setter
@Document(collection = "mesas")
@CompoundIndexes({
        @CompoundIndex(name = "unique_numero_idfechamento", def = "{'numero': 1, 'idfechamento': 1}", unique = true)
})
public class Mesa {
    @Id
    private String id;

    @Min(value = 1, message = "Número da mesa deve ser maior que zero")
    private int numero;

    @NotNull(message = "Status é obrigatório")
    private StatusMesa status;



    @NotBlank(message = "O ID do fechamento é obrigatório")
    private String idfechamento;
}
