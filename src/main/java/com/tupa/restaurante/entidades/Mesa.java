package com.tupa.restaurante.entidades;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@Document(collection = "mesas")
public class Mesa {
    @Id
    private String id;

    @Min(value = 1, message = "Número da mesa deve ser maior que zero")
    @Indexed(unique = true)
    private int numero;

    @NotBlank(message = "Status é obrigatório")
    private String status;
}
