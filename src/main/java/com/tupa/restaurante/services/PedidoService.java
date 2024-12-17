package com.tupa.restaurante.services;

import com.tupa.restaurante.dto.PedidoDTORetorno;
import com.tupa.restaurante.entidades.Conta;
import com.tupa.restaurante.entidades.Fechamento;
import com.tupa.restaurante.entidades.Pedido;
import com.tupa.restaurante.entidades.Status;
import com.tupa.restaurante.repository.ContaRepository;
import com.tupa.restaurante.repository.MesaRepository;
import com.tupa.restaurante.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private FechamentoService fechamentoService;
    @Autowired
    private MesaRepository mesaRepository;

    /**
     * Retorna todos os pedidos associados ao fechamento atualmente aberto como PedidoDTORetorno.
     *
     * @return Lista de PedidoDTORetorno.
     */
    public List<PedidoDTORetorno> getAllPedidos() {
        Optional<Fechamento> fechamentoAbertoOpt = fechamentoService.obterFechamentoAberto();

        if (fechamentoAbertoOpt.isPresent()) {
            Fechamento fechamentoAberto = fechamentoAbertoOpt.get();
            String idFechamentoAberto = fechamentoAberto.getId();

            // Recupera as contas associadas ao fechamento aberto
            List<Conta> contas = contaRepository.findByIdFechamento(idFechamentoAberto);

            if (!contas.isEmpty()) {
                List<String> idContas = contas.stream()
                        .map(Conta::getId)
                        .collect(Collectors.toList());

                // Recupera os pedidos associados às contas filtradas
                List<Pedido> pedidos = pedidoRepository.findByIdContaIn(idContas);

                // Mapeia os pedidos para DTOs
                return pedidos.stream()
                        .map(this::convertToPedidoDTO)
                        .collect(Collectors.toList());
            }
        }

        // Retorna uma lista vazia se não houver fechamento aberto ou contas associadas
        return Collections.emptyList();
    }

    /**
     * Converte um Pedido para PedidoDTORetorno.
     *
     * @param pedido Pedido a ser convertido.
     * @return PedidoDTORetorno.
     */
    public PedidoDTORetorno convertToPedidoDTO(Pedido pedido) {
        PedidoDTORetorno dto = new PedidoDTORetorno();
        dto.setIdPedido(pedido.getId());
        dto.setProdutos(pedido.getProdutos());
        dto.setObservacao(pedido.getObservacao());
        dto.setStatus(pedido.getStatus().name());

        // Validação para evitar IDs nulos
        if (pedido.getIdConta() != null) {
            Conta conta = contaRepository.findById(pedido.getIdConta()).orElse(null);
            if (conta != null) {
                dto.setIdConta(conta.getId());
                if (conta.getIdMesa() != null) {
                    dto.setNumeroMesa(mesaRepository.findById(conta.getIdMesa())
                            .map(mesa -> mesa.getNumero())
                            .orElse(0));
                }
            }
        }

        return dto;
    }

    public PedidoDTORetorno avancarEstadoPedido(String idPedido) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(idPedido);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();

            // Avançar o estado
            switch (pedido.getStatus()) {
                case PENDENTE:
                    pedido.setStatus(Status.PREPARANDO);
                    break;
                case PREPARANDO:
                    pedido.setStatus(Status.PRONTO);
                    break;
                default:
                    throw new IllegalStateException("Pedido já está no estado final.");
            }

            // Salvar o pedido atualizado
            pedidoRepository.save(pedido);

            // Retornar como DTO
            return convertToPedidoDTO(pedido);
        } else {
            throw new IllegalArgumentException("Pedido não encontrado com ID: " + idPedido);
        }
    }
}
