package com.tupa.restaurante.repository;

import com.tupa.restaurante.entities.Fechamento;
import com.tupa.restaurante.entities.enums.StatusFechamento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FechamentoRepository extends MongoRepository<Fechamento, String> {

    Optional<Fechamento> findByStatus(StatusFechamento status);
}
