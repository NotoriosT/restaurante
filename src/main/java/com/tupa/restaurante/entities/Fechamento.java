package com.tupa.restaurante.entities;

import com.tupa.restaurante.entities.enums.StatusFechamento;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "fechamentos")
public class Fechamento {
    @Id
    private String id; // Este ID será gerado automaticamente pelo MongoDB

    private LocalDateTime dataAbertura;

    private LocalDateTime dataFechamento;



    private StatusFechamento status;
}
