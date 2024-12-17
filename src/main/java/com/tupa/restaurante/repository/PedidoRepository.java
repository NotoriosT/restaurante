package com.tupa.restaurante.repository;

import com.tupa.restaurante.entidades.Pedido;
import com.tupa.restaurante.entidades.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PedidoRepository extends MongoRepository<Pedido, String> {

    List<Pedido> findByIdConta(String idConta);
    List<Pedido> findByIdContaIn(List<String> idContas);
}
