package com.tupa.restaurante.repository;

import com.tupa.restaurante.entidades.Produto;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface ProdutoRepository extends MongoRepository<Produto, String> {
    // Métodos de consulta personalizados, se necessário
}

