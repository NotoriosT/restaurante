package com.tupa.restaurante.repository;

import com.tupa.restaurante.entities.Mesa;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MesaRepository extends MongoRepository<Mesa, String> {
    List<Mesa> findByIdFechamento(String id);
}

