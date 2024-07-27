package com.tupa.restaurante.repository;

import com.tupa.restaurante.entidades.Pedido;
import com.tupa.restaurante.entidades.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PedidoRepository extends MongoRepository<Pedido, String> {
    List<Pedido> findByMesaId(int mesaId);
    List<Pedido> findByStatusIn(List<Status> statuses);
}
