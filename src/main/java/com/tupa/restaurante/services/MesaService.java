package com.tupa.restaurante.services;



import com.tupa.restaurante.entidades.Pedido;
import com.tupa.restaurante.entidades.ProdutoPedido;
import com.tupa.restaurante.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesaService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public double calcularGastoPorMesa(int mesaId) {
        List<Pedido> pedidos = pedidoRepository.findByMesaId(mesaId);
        return pedidos.stream()
                .flatMap(pedido -> pedido.getProdutos().stream())
                .mapToDouble(ProdutoPedido::getTotal)
                .sum();
    }
}
