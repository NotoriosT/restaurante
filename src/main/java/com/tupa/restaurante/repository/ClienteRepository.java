package com.tupa.restaurante.repository;

import com.tupa.restaurante.entidades.Cliente;
import com.tupa.restaurante.entidades.Colaborador;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClienteRepository extends MongoRepository<Cliente, String> {
}
