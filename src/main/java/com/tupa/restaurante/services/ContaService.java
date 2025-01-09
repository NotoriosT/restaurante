package com.tupa.restaurante.services;

import com.tupa.restaurante.exeptions.ClienteNaoEncontradoException;
import com.tupa.restaurante.entidades.*;
import com.tupa.restaurante.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private FechamentoService fechamentoService;

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    ProdutoRepository produtoRepository;

    public Conta abrirConta(String idMesa, String idCliente) {
        // Validações iniciais
        if (idCliente == null || idCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("É obrigatório informar um cliente para abrir a conta.");
        }

        // Verifica se a mesa existe
        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new IllegalArgumentException("Mesa não encontrada"));

        // Verifica se o cliente existe
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado"));

        // Verifica se já existe uma conta aberta para essa mesa
        if (contaRepository.findByIdMesaAndStatus(idMesa, StatusConta.ABERTA).isPresent()) {
            throw new IllegalStateException("Já existe uma conta aberta para esta mesa.");
        }

        // Cria nova conta
        Conta conta = new Conta();
        conta.setIdMesa(idMesa);
        conta.setDataAbertura(LocalDateTime.now());
        conta.setStatus(StatusConta.ABERTA);
        conta.setIdCliente(idCliente); // Registra o ID do cliente

        Optional<Fechamento> fechamento = fechamentoService.obterFechamentoAberto();
        fechamento.ifPresent(value -> conta.setIdFechamento(value.getId()));

        // Salva a conta primeiro para obter o ID gerado
        Conta contaSalva = contaRepository.save(conta);

        // Atualiza a mesa com o ID da conta atual
        mesa.setIdContaAtual(contaSalva.getId());
        // Atualiza o status da mesa para OCUPADO
        mesa.setStatus(StatusMesa.OCUPADO);
        mesaRepository.save(mesa);

        return contaSalva;
    }

    public Pedido criarPedido(String idConta, Pedido novoPedido) {
        // Verifica se a conta existe e está aberta
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
        if (conta.getStatus() != StatusConta.ABERTA) {
            throw new IllegalStateException("Não é possível adicionar pedidos a uma conta fechada.");
        }

        if (novoPedido.getProdutos() == null || novoPedido.getProdutos().isEmpty()) {
            throw new IllegalArgumentException("A lista de produtos não pode estar vazia");
        }

        // Atualiza os produtos com base no ID do produto
        for (ProdutoPedido produtoPedido : novoPedido.getProdutos()) {
            Produto produto = produtoRepository.findById(produtoPedido.getIdProduto())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoPedido.getIdProduto()));

            produtoPedido.setTotal(produto.getPreco().multiply(BigDecimal.valueOf(produtoPedido.getQuantidade())));
        }

        novoPedido.setIdConta(idConta);
        novoPedido.setStatus(Status.PENDENTE);
        return pedidoRepository.save(novoPedido);
    }


    public BigDecimal fecharConta(String idConta) {
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        if (conta.getStatus() == StatusConta.FECHADA) {
            throw new IllegalStateException("Esta conta já foi fechada.");
        }

        List<Pedido> pedidosDaConta = pedidoRepository.findByIdConta(idConta);

        BigDecimal total = new BigDecimal(0);
        for (Pedido pedido : pedidosDaConta) {
            for (ProdutoPedido pp : pedido.getProdutos()) {
                // Calcula o total de cada ProdutoPedido
                total= pp.getTotal();
            }
        }

        // Atualiza a conta para FECHADA
        conta.setStatus(StatusConta.FECHADA);
        conta.setDataFechamento(LocalDateTime.now());
        conta.setTotal(total);
        contaRepository.save(conta);

        // Recupera a mesa associada
        Mesa mesa = mesaRepository.findById(conta.getIdMesa())
                .orElseThrow(() -> new IllegalArgumentException("Mesa não encontrada"));

        // Atualiza o status da mesa para DISPONIVEL e remove o idContaAtual
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
