package com.tupa.restaurante.service;

import com.tupa.restaurante.dto.ChartDataDTO;
import com.tupa.restaurante.dto.PedidoDTORetorno;
import com.tupa.restaurante.dto.ProdutoPedidoDTO;
import com.tupa.restaurante.entities.*;
import com.tupa.restaurante.entities.enums.Status;
import com.tupa.restaurante.exceptions.PedidoNotFoundException;
import com.tupa.restaurante.exceptions.PedidoStateException;
import com.tupa.restaurante.exceptions.ProdutoPedidoNotFoundException;
import com.tupa.restaurante.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final ContaRepository contaRepository;
    private final FechamentoService fechamentoService;
    private final ClienteRepository clienteRepository;
    private final MesaRepository mesaRepository;
    private final ProdutoRepository produtoRepository;
    private final ServicoWebSocketPedido servicoWebSocketPedido;

    public PedidoService(PedidoRepository pedidoRepository,
                         ContaRepository contaRepository,
                         FechamentoService fechamentoService,
                         ClienteRepository clienteRepository,
                         MesaRepository mesaRepository,
                         ProdutoRepository produtoRepository,
                         ServicoWebSocketPedido servicoWebSocketPedido) {
        this.pedidoRepository = pedidoRepository;
        this.contaRepository = contaRepository;
        this.fechamentoService = fechamentoService;
        this.clienteRepository = clienteRepository;
        this.mesaRepository = mesaRepository;
        this.produtoRepository = produtoRepository;
        this.servicoWebSocketPedido = servicoWebSocketPedido;
    }

    /**
     * Retorna todos os pedidos associados ao fechamento atualmente aberto como PedidoDTORetorno.
     *
     * @return Lista de PedidoDTORetorno.
     */
    public List<PedidoDTORetorno> getAllPedidos() {
        logger.info("Obtendo todos os pedidos associados ao fechamento aberto.");
        Optional<Fechamento> fechamentoAbertoOpt = fechamentoService.obterFechamentoAberto();

        if (fechamentoAbertoOpt.isPresent()) {
            Fechamento fechamentoAberto = fechamentoAbertoOpt.get();
            String idFechamentoAberto = fechamentoAberto.getId();

            List<Conta> contas = contaRepository.findByIdFechamento(idFechamentoAberto);
            if (!contas.isEmpty()) {
                List<String> idContas = contas.stream()
                        .map(Conta::getId)
                        .collect(Collectors.toList());

                List<Pedido> pedidos = pedidoRepository.findByIdContaIn(idContas);
                List<PedidoDTORetorno> dtos = pedidos.stream()
                        .map(this::convertToPedidoDTO)
                        .collect(Collectors.toList());

                dtos.forEach(dto -> dto.setIdFechamento(idFechamentoAberto));
                logger.info("Total de pedidos encontrados: {}", dtos.size());

                return dtos;
            }
        }

        logger.warn("Nenhum fechamento aberto ou nenhuma conta associada encontrada.");
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

        dto.setIdPedido(pedido.getId());
        dto.setObservacao(pedido.getObservacao());
        dto.setStatus(pedido.getStatus().name());
        dto.setDataPedido(pedido.getDataPedido());
        dto.setIdConta(pedido.getIdConta());

        List<ProdutoPedidoDTO> produtosDTO = pedido.getProdutos().stream().map(produtoPedido -> {
            Produto produto = produtoRepository.findById(produtoPedido.getIdProduto())
                    .orElseThrow(() -> new ProdutoPedidoNotFoundException("Produto não encontrado com ID: " + produtoPedido.getIdProduto()));

            ProdutoPedidoDTO produtoPedidoDTO = new ProdutoPedidoDTO();
            produtoPedidoDTO.setProduto(produto);
            produtoPedidoDTO.setQuantidade(produtoPedido.getQuantidade());
            produtoPedidoDTO.setTotal(produtoPedido.getTotal());
            produtoPedidoDTO.setObservacao(produtoPedido.getObservacao());

            return produtoPedidoDTO;
        }).collect(Collectors.toList());

        dto.setProdutos(produtosDTO);
        dto.setCancelamentoProdutoPedidosList(pedido.getCancelamentos());

        // Calcular o total do pedido
        BigDecimal total = produtosDTO.stream()
                .map(ProdutoPedidoDTO::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotal(total);

        // Obter informações adicionais se necessário (e.g., numeroMesa, nomeCliente)
        Optional<Conta> contaOpt = contaRepository.findById(pedido.getIdConta());
        contaOpt.ifPresent(conta -> {
            Optional<Mesa> mesaOpt = mesaRepository.findById(conta.getIdMesa());
            mesaOpt.ifPresent(mesa -> dto.setNumeroMesa(mesa.getNumero()));
            Optional<Cliente> clienteOpt = clienteRepository.findById(conta.getIdCliente());
            clienteOpt.ifPresent(cliente -> dto.setNomeCliente(cliente.getNome()));
            dto.setIdMesa(conta.getIdMesa());
        });

        return dto;
    }

    /**
     * Avança o estado de um pedido.
     *
     * @param idPedido ID do pedido.
     * @return Pedido atualizado como DTO.
     */
    @Transactional
    public PedidoDTORetorno avancarEstadoPedido(String idPedido) {
        logger.info("Avançando estado do pedido com ID: {}", idPedido);
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido não encontrado com ID: " + idPedido));

        switch (pedido.getStatus()) {
            case PENDENTE:
                pedido.setStatus(Status.PREPARANDO);
                break;
            case PREPARANDO:
                pedido.setStatus(Status.PRONTO);
                break;
            default:
                logger.warn("Tentativa de avançar estado de pedido já no estado final: {}", idPedido);
                throw new PedidoStateException("Pedido já está no estado final.");
        }

        pedidoRepository.save(pedido);
        logger.info("Estado do pedido atualizado para: {}", pedido.getStatus());

        // Enviar atualização via WebSocket
        List<PedidoDTORetorno> listaPedidosAtualizada = getAllPedidos();
        servicoWebSocketPedido.enviarPedidosAtualizadosParaCozinha(listaPedidosAtualizada);
        logger.info("Atualizações de pedidos enviadas para a cozinha via WebSocket.");

        return convertToPedidoDTO(pedido);
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
    @Transactional
    public PedidoDTORetorno cancelarProdutoNoPedido(String idPedido, String idProduto, int quantidade, String canceladoPor, String motivo) {
        logger.info("Cancelando {} unidades do produto {} no pedido {} por {}", quantidade, idProduto, idPedido, canceladoPor);
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new PedidoNotFoundException("Pedido não encontrado com ID: " + idPedido));

        ProdutoPedido produtoPedido = pedido.getProdutos().stream()
                .filter(pp -> pp.getIdProduto().equals(idProduto))
                .findFirst()
                .orElseThrow(() -> new ProdutoPedidoNotFoundException("Produto não encontrado no pedido."));

        if (quantidade <= 0) {
            logger.error("Quantidade a cancelar deve ser maior que zero.");
            throw new IllegalArgumentException("Quantidade a cancelar deve ser maior que zero.");
        }

        if (quantidade > produtoPedido.getQuantidade()) {
            logger.error("Quantidade a cancelar excede a quantidade atual do produto.");
            throw new IllegalArgumentException("Quantidade a cancelar excede a quantidade atual do produto.");
        }

        // Registrar o cancelamento
        CancelamentoProdutoPedido cancelamento = new CancelamentoProdutoPedido();
        cancelamento.setIdPedido(idPedido);
        cancelamento.setDataCancelamento(LocalDateTime.now());
        cancelamento.setCanceladoPor(canceladoPor);
        cancelamento.setIdProduto(idProduto);
        cancelamento.setQuantidadeCancelada(quantidade);
        cancelamento.setMotivo(motivo);
        pedido.getCancelamentos().add(cancelamento);

        // Atualizar a quantidade no ProdutoPedido
        produtoPedido.setQuantidade(produtoPedido.getQuantidade() - quantidade);

        // Atualizar o total do ProdutoPedido
        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new ProdutoPedidoNotFoundException("Produto não encontrado com ID: " + idProduto));
        produtoPedido.setTotal(produto.getPreco().multiply(BigDecimal.valueOf(produtoPedido.getQuantidade())));

        // Se a quantidade for zero, remover o produto do pedido
        if (produtoPedido.getQuantidade() == 0) {
            pedido.getProdutos().remove(produtoPedido);
            logger.info("Produto removido do pedido devido à quantidade zerada: {}", idProduto);
        }

        // Salvar o pedido atualizado
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        logger.info("Pedido atualizado após cancelamento: {}", idPedido);

        // Enviar atualização via WebSocket
        List<PedidoDTORetorno> listaPedidosAtualizada = getAllPedidos();
        servicoWebSocketPedido.enviarPedidosAtualizadosParaCozinha(listaPedidosAtualizada);
        logger.info("Atualizações de pedidos enviadas para a cozinha via WebSocket após cancelamento.");

        return convertToPedidoDTO(pedidoAtualizado);
    }

    /**
     * Retorna a lista de pedidos associados a uma conta específica como DTOs.
     *
     * @param idConta ID da conta.
     * @return Lista de PedidoDTORetorno.
     */
    public List<PedidoDTORetorno> getPedidosByContaId(String idConta) {
        logger.info("Obtendo pedidos para a conta ID: {}", idConta);
        List<Pedido> pedidos = pedidoRepository.findByIdConta(idConta);
        List<PedidoDTORetorno> dtos = pedidos.stream()
                .map(this::convertToPedidoDTO)
                .collect(Collectors.toList());
        logger.info("Total de pedidos encontrados para a conta {}: {}", idConta, dtos.size());
        return dtos;
    }


    public ChartDataDTO getVendasPorDia(LocalDate startData, LocalDate endData) {
        List<Pedido> pedidos = pedidoRepository.findAll().stream()
                .filter(p -> {
                    LocalDate dataPedido = p.getDataPedido().toLocalDate();
                    return (dataPedido.isEqual(startData) || dataPedido.isAfter(startData)) &&
                            (dataPedido.isEqual(endData) || dataPedido.isBefore(endData)) &&
                            p.getStatus() == Status.PRONTO; // Considerando apenas pedidos fechados
                })
                .collect(Collectors.toList());

        Map<LocalDate, BigDecimal> vendasPorDia = new TreeMap<>();

        for (Pedido pedido : pedidos) {
            LocalDate data = pedido.getDataPedido().toLocalDate();
            BigDecimal totalPedido = pedido.getProdutos().stream()
                    .map(pp -> pp.getTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vendasPorDia.put(data, vendasPorDia.getOrDefault(data, BigDecimal.ZERO).add(totalPedido));
        }

        ChartDataDTO chartData = new ChartDataDTO();
        chartData.setLabels(new ArrayList<>());
        chartData.setDatasets(new ArrayList<>());

        ChartDataDTO.Dataset dataset = new ChartDataDTO.Dataset();
        dataset.setLabel("Vendas Diárias");
        dataset.setData(new ArrayList<>());
        dataset.setBackgroundColor(new ArrayList<>());

        Random random = new Random();

        for (Map.Entry<LocalDate, BigDecimal> entry : vendasPorDia.entrySet()) {
            chartData.getLabels().add(entry.getKey().toString());
            dataset.getData().add(entry.getValue());
            dataset.getBackgroundColor().add(String.format("rgba(%d,%d,%d,0.7)", random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }

        chartData.getDatasets().add(dataset);
        return chartData;
    }

    public ChartDataDTO getPedidosPorStatus() {
        List<Pedido> pedidos = pedidoRepository.findAll();

        Map<String, Long> statusCountMap = pedidos.stream()
                .collect(Collectors.groupingBy(p -> p.getStatus().name(), Collectors.counting()));

        ChartDataDTO chartData = new ChartDataDTO();
        chartData.setLabels(new ArrayList<>());
        chartData.setDatasets(new ArrayList<>());

        ChartDataDTO.Dataset dataset = new ChartDataDTO.Dataset();
        dataset.setLabel("Pedidos por Status");
        dataset.setData(new ArrayList<>());
        dataset.setBackgroundColor(new ArrayList<>());

        List<String> predefinedColors = List.of(
                "rgba(255,99,132,0.7)",  // Vermelho
                "rgba(54,162,235,0.7)", // Azul
                "rgba(255,206,86,0.7)", // Amarelo
                "rgba(75,192,192,0.7)", // Verde
                "rgba(153,102,255,0.7)" // Roxo
        );
        int index = 0;

        for (Map.Entry<String, Long> entry : statusCountMap.entrySet()) {
            chartData.getLabels().add(entry.getKey());
            dataset.getData().add(entry.getValue());
            dataset.getBackgroundColor().add(predefinedColors.get(index % predefinedColors.size()));
            index++;
        }

        chartData.getDatasets().add(dataset);
        return chartData;
    }
}



