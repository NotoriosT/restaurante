package com.tupa.restaurante.services;

import com.tupa.restaurante.dto.PedidoDTORetorno;
import com.tupa.restaurante.dto.ProdutoPedidoDTO;
import com.tupa.restaurante.entidades.*;
import com.tupa.restaurante.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private ServicoWebSocketPedido servicoWebSocketPedido;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

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
                // Extrai os IDs das contas
                List<String> idContas = contas.stream()
                        .map(Conta::getId)
                        .toList();

                // Recupera os pedidos associados às contas
                List<Pedido> pedidos = pedidoRepository.findByIdContaIn(idContas);

                // Mapeia os pedidos para DTOs
                List<PedidoDTORetorno> dtos = pedidos.stream()
                        .map(this::convertToPedidoDTO)
                        .collect(Collectors.toList());

                // Define o idFechamento em cada DTO
                dtos.forEach(dto -> dto.setIdFechamento(idFechamentoAberto));

                return dtos;
            }
        }

        // Retorna lista vazia se não houver fechamento aberto ou nenhuma conta
        return List.of();
    }

    /**
     * Converte um Pedido para PedidoDTORetorno.
     *
     * @param pedido Pedido a ser convertido.
     * @return PedidoDTORetorno.
     */
    public PedidoDTORetorno convertToPedidoDTO(Pedido pedido) {
        PedidoDTORetorno dto = new PedidoDTORetorno();

        // Campos básicos
        dto.setIdPedido(pedido.getId());
        dto.setObservacao(pedido.getObservacao());
        dto.setStatus(pedido.getStatus().name());
        dto.setDataPedido(pedido.getDataPedido());

        // Converte os ProdutoPedido para DTOs
        List<ProdutoPedidoDTO> produtosDTO = pedido.getProdutos().stream().map(produtoPedido -> {
            Produto produto = produtoRepository.findById(produtoPedido.getIdProduto())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + produtoPedido.getIdProduto()));

            ProdutoPedidoDTO produtoPedidoDTO = new ProdutoPedidoDTO();
            produtoPedidoDTO.setProduto(produto);
            produtoPedidoDTO.setQuantidade(produtoPedido.getQuantidade());
            produtoPedidoDTO.setTotal(produtoPedido.getTotal());
            produtoPedidoDTO.setObservacao(produtoPedido.getObservacao());

            return produtoPedidoDTO;
        }).collect(Collectors.toList());

        dto.setProdutos(produtosDTO);

        // Adiciona os cancelamentos ao DTO
        dto.setCancelamentoProdutoPedidosList(pedido.getCancelamentos());

        return dto;
    }


    /**
     * Avança o estado de um pedido.
     *
     * @param idPedido ID do pedido.
     * @return Pedido atualizado como DTO.
     */
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

            // Enviar atualização via WebSocket
            List<PedidoDTORetorno> listaPedidosAtualizada = getAllPedidos();
            servicoWebSocketPedido.enviarPedidosAtualizadosParaCozinha(listaPedidosAtualizada);

            // Retornar como DTO
            return convertToPedidoDTO(pedido);
        } else {
            throw new IllegalArgumentException("Pedido não encontrado com ID: " + idPedido);
        }
    }

    /**
     * Método para cancelar uma quantidade específica de um produto dentro de um pedido.
     *
     * @param idPedido        ID do pedido.
     * @param idProduto       ID do ProdutoPedido dentro do pedido.
     * @param quantidade      Quantidade a ser cancelada.
     * @param canceladoPor    Identificador de quem está cancelando (por exemplo, ID do usuário).
     * @param motivo          Motivo do cancelamento (opcional).
     * @return Pedido atualizado como DTO.
     */
    public PedidoDTORetorno cancelarProdutoNoPedido(String idPedido, String idProduto, int quantidade, String canceladoPor, String motivo) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com ID: " + idPedido));

        Optional<ProdutoPedido> produtoPedidoOpt = pedido.getProdutos().stream()
                .filter(pp -> pp.getIdProduto().equals(idProduto))
                .findFirst();

        if (produtoPedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado no pedido.");
        }

        ProdutoPedido produtoPedido = produtoPedidoOpt.get();

        // Validações
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a cancelar deve ser maior que zero.");
        }
        if (quantidade > produtoPedido.getQuantidade()) {
            throw new IllegalArgumentException("Quantidade a cancelar excede a quantidade atual do produto.");
        }

        // Registrar o cancelamento diretamente no pedido
        CancelamentoProdutoPedido cancelamento = new CancelamentoProdutoPedido();
        cancelamento.setIdPedido(idPedido);
        cancelamento.setDataCancelamento(LocalDateTime.now());
        cancelamento.setCanceladoPor(canceladoPor);
        cancelamento.setIdProduto(produtoPedido.getIdProduto());
        cancelamento.setQuantidadeCancelada(quantidade);
        cancelamento.setMotivo(motivo);
        pedido.getCancelamentos().add(cancelamento);

        // Atualizar a quantidade no ProdutoPedido
        produtoPedido.setQuantidade(produtoPedido.getQuantidade() - quantidade);

        // Atualizar o total do ProdutoPedido
        Produto produto = produtoRepository.findById(produtoPedido.getIdProduto())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + produtoPedido.getIdProduto()));
        produtoPedido.setTotal(produto.getPreco().multiply(BigDecimal.valueOf(produtoPedido.getQuantidade())));

        // Se a quantidade for zero, remover o produto do pedido
        if (produtoPedido.getQuantidade() == 0) {
            pedido.getProdutos().remove(produtoPedido);
        }

        // Salvar o pedido atualizado
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        // Enviar atualização via WebSocket
        List<PedidoDTORetorno> listaPedidosAtualizada = getAllPedidos();
        servicoWebSocketPedido.enviarPedidosAtualizadosParaCozinha(listaPedidosAtualizada);

        return convertToPedidoDTO(pedidoAtualizado);
    }


    /**
     * Retorna a lista de IDs dos pedidos associados a uma conta específica.
     *
     * @param idConta ID da conta.
     * @return Lista de IDs dos pedidos.
     */
    public List<String> getPedidoIdsByContaId(String idConta) {
        List<Pedido> pedidos = pedidoRepository.findByIdConta(idConta);
        return pedidos.stream()
                .map(Pedido::getId)
                .collect(Collectors.toList());
    }

    /**
     * Retorna a lista de pedidos associados a uma conta específica como DTOs.
     *
     * @param idConta ID da conta.
     * @return Lista de PedidoDTORetorno.
     */
    public List<PedidoDTORetorno> getPedidosByContaId(String idConta) {
        List<Pedido> pedidos = pedidoRepository.findByIdConta(idConta);
        return pedidos.stream()
                .map(this::convertToPedidoDTO)
                .collect(Collectors.toList());
    }
}
