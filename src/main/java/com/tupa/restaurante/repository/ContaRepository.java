package com.tupa.restaurante.repository;

import com.tupa.restaurante.entidades.Conta;
import com.tupa.restaurante.entidades.StatusConta;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ContaRepository extends MongoRepository<Conta, String> {
    Optional<Conta> findByIdMesaAndStatus(String idMesa, com.tupa.restaurante.entidades.StatusConta status);
    List<Conta> findByIdMesa(String idMesa);
    List<Conta> findByIdFechamento(String idFechamento);
}
