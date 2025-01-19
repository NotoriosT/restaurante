package com.tupa.restaurante.repository;

import com.tupa.restaurante.entities.Pedido;
import com.tupa.restaurante.entities.enums.Status;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PedidoRepository extends MongoRepository<Pedido, String> {

    List<Pedido> findByIdConta(String idConta);
    List<Pedido> findByIdContaIn(List<String> idContas);

    @Aggregation(pipeline = {
            "{ '$unwind': '$produtos' }",
            "{ '$group': { '_id': '$produtos.idProduto', 'totalVendido': { '$sum': '$produtos.quantidade' } } }",
            "{ '$sort': { 'totalVendido': -1 } }"
    })
    List<SalesPerProduct> aggregateSalesPerProduct();

    interface SalesPerProduct {
        String getId();
        Integer getTotalVendido();
    }

}
