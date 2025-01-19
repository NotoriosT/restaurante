package com.tupa.restaurante.repository;

import com.tupa.restaurante.entities.Cliente;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClienteRepository extends MongoRepository<Cliente, String> {
}
