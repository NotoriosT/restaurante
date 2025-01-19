package com.tupa.restaurante.repository;

import com.tupa.restaurante.entities.Produto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public interface ProdutoRepository extends MongoRepository<Produto, String> {
    // Métodos de consulta personalizados, se necessário



    default Map<String, String> getProductNamesMap() {
        List<Produto> produtos = findAll();
        return produtos.stream()
                .collect(Collectors.toMap(Produto::getId, Produto::getNome));
    }
}

