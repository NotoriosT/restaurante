package com.tupa.restaurante.repository;

import com.tupa.restaurante.entities.Conta;
import com.tupa.restaurante.entities.enums.StatusConta;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ContaRepository extends MongoRepository<Conta, String> {
    Optional<Conta> findByIdMesaAndStatus(String idMesa, StatusConta status);
    List<Conta> findByIdMesa(String idMesa);
    List<Conta> findByIdFechamento(String idFechamento);















    @Aggregation(pipeline = {
            "{ '$match': { 'status': 'FECHADA' } }",
            "{ '$group': { '_id': { '$dateToString': { 'format': '%Y-%m-%d', 'date': '$dataFechamento' } }, 'totalVendas': { '$sum': '$total' } } }",
            "{ '$sort': { '_id': 1 } }"
    })
    List<SalesPerDay> aggregateSalesPerDay();

    interface SalesPerDay {
        String getId();
        BigDecimal getTotalVendas();
    }

    @Aggregation(pipeline = {
            "{ '$group': { '_id': '$status', 'count': { '$sum': 1 } } }"
    })
    List<AccountStatusCount> aggregateAccountStatus();

    interface AccountStatusCount {
        StatusConta getId();
        Long getCount();
    }











}
