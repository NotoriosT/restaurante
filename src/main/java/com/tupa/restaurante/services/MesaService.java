package com.tupa.restaurante.services;



import com.mongodb.DuplicateKeyException;
import com.tupa.restaurante.entidades.Mesa;
import com.tupa.restaurante.entidades.Pedido;
import com.tupa.restaurante.entidades.ProdutoPedido;
import com.tupa.restaurante.repository.MesaRepository;
import com.tupa.restaurante.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesaService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private MesaRepository mesaRepository;

    public double calcularGastoPorMesa(int mesaId) {
        List<Pedido> pedidos = pedidoRepository.findByMesaId(mesaId);
        return pedidos.stream()
                .flatMap(pedido -> pedido.getProdutos().stream())
                .mapToDouble(ProdutoPedido::getTotal)
                .sum();
    }
    public Mesa createMesa(Mesa mesa) {
        try {
            return mesaRepository.save(mesa);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("Número da mesa já existe!");
        }
    }
}
