package com.tupa.restaurante.repository;

import com.tupa.restaurante.entidades.Mesa;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MesaRepository extends MongoRepository<Mesa, String> {
}

