package com.tupa.restaurante.service;

import com.tupa.restaurante.entities.enums.Status;
import com.tupa.restaurante.entities.enums.StatusConta;
import com.tupa.restaurante.entities.enums.StatusMesa;
import com.tupa.restaurante.exceptions.ClienteNaoEncontradoException;
import com.tupa.restaurante.entities.*;
import com.tupa.restaurante.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final MesaRepository mesaRepository;
    private final PedidoRepository pedidoRepository;
    private final FechamentoService fechamentoService;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public ContaService(ContaRepository contaRepository, MesaRepository mesaRepository, PedidoRepository pedidoRepository,
                        FechamentoService fechamentoService, ClienteRepository clienteRepository, ProdutoRepository produtoRepository) {
        this.contaRepository = contaRepository;
        this.mesaRepository = mesaRepository;
        this.pedidoRepository = pedidoRepository;
        this.fechamentoService = fechamentoService;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Conta abrirConta(String idMesa, String idCliente) {
        if (idCliente == null || idCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("É obrigatório informar um cliente para abrir a conta.");
        }

        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new IllegalArgumentException("Mesa não encontrada"));

        clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado"));

        if (contaRepository.findByIdMesaAndStatus(idMesa, StatusConta.ABERTA).isPresent()) {
            throw new IllegalStateException("Já existe uma conta aberta para esta mesa.");
        }

        Conta conta = new Conta();
        conta.setIdMesa(idMesa);
        conta.setDataAbertura(LocalDateTime.now());
        conta.setStatus(StatusConta.ABERTA);
        conta.setIdCliente(idCliente);

        fechamentoService.obterFechamentoAberto().ifPresent(fechamento -> conta.setIdFechamento(fechamento.getId()));

        Conta contaSalva = contaRepository.save(conta);

        Mesa mesaAtualizada = mesaRepository.findById(idMesa).get();
        mesaAtualizada.setIdContaAtual(contaSalva.getId());
        mesaAtualizada.setStatus(StatusMesa.OCUPADO);
        mesaRepository.save(mesaAtualizada);

        return contaSalva;
    }

    @Transactional
    public Pedido criarPedido(String idConta, Pedido novoPedido) {
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
        if (conta.getStatus() != StatusConta.ABERTA) {
            throw new IllegalStateException("Não é possível adicionar pedidos a uma conta fechada.");
        }

        if (novoPedido.getProdutos() == null || novoPedido.getProdutos().isEmpty()) {
            throw new IllegalArgumentException("A lista de produtos não pode estar vazia");
        }

        for (ProdutoPedido produtoPedido : novoPedido.getProdutos()) {
            Produto produto = produtoRepository.findById(produtoPedido.getIdProduto())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoPedido.getIdProduto()));
            produtoPedido.setTotal(produto.getPreco().multiply(BigDecimal.valueOf(produtoPedido.getQuantidade())));
        }

        novoPedido.setIdConta(idConta);
        novoPedido.setStatus(Status.PENDENTE);
        return pedidoRepository.save(novoPedido);
    }

    @Transactional
    public BigDecimal fecharConta(String idConta) {
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        if (conta.getStatus() == StatusConta.FECHADA) {
            throw new IllegalStateException("Esta conta já foi fechada.");
        }

        List<Pedido> pedidosDaConta = pedidoRepository.findByIdConta(idConta);

        BigDecimal total = BigDecimal.ZERO;
        for (Pedido pedido : pedidosDaConta) {
            for (ProdutoPedido pp : pedido.getProdutos()) {
                total = total.add(pp.getTotal());
            }
        }

        conta.setStatus(StatusConta.FECHADA);
        conta.setDataFechamento(LocalDateTime.now());
        conta.setTotal(total);
        contaRepository.save(conta);

        Mesa mesa = mesaRepository.findById(conta.getIdMesa())
                .orElseThrow(() -> new IllegalArgumentException("Mesa não encontrada"));

        mesa.setStatus(StatusMesa.DISPONIVEL);
        mesa.setIdContaAtual(null);
        mesaRepository.save(mesa);

        return total;
    }

    public List<Conta> getAllContas() {
        return contaRepository.findAll();
    }

    public List<Conta> getContasByMesaId(String idMesa) {
        return contaRepository.findByIdMesa(idMesa);
    }
}
