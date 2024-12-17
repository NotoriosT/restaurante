package com.tupa.restaurante.services;

import com.tupa.restaurante.entidades.*;
import com.tupa.restaurante.repository.ContaRepository;
import com.tupa.restaurante.repository.MesaRepository;
import com.tupa.restaurante.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Conta abrirConta(String idMesa) {
        // Verifica se a mesa existe
        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new IllegalArgumentException("Mesa não encontrada"));

        // Verifica se já existe uma conta aberta para essa mesa
        if (contaRepository.findByIdMesaAndStatus(idMesa, StatusConta.ABERTA).isPresent()) {
            throw new IllegalStateException("Já existe uma conta aberta para esta mesa.");
        }

        // Cria nova conta
        Conta conta = new Conta();
        conta.setIdMesa(idMesa);
        conta.setDataAbertura(LocalDateTime.now());
        conta.setStatus(StatusConta.ABERTA);
      Optional<Fechamento>fechamento= fechamentoService.obterFechamentoAberto();
        fechamento.ifPresent(value -> conta.setIdFechamento(value.getId()));
        // Atualiza o status da mesa para OCUPADA (por exemplo)
        mesa.setStatus(StatusMesa.OCUPADO);
        mesaRepository.save(mesa);

        return contaRepository.save(conta);
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

        novoPedido.setIdConta(idConta);
        novoPedido.setStatus(Status.PENDENTE);
        return pedidoRepository.save(novoPedido);
    }

    public double fecharConta(String idConta) {
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

        if (conta.getStatus() == StatusConta.FECHADA) {
            throw new IllegalStateException("Esta conta já foi fechada.");
        }

        List<Pedido> pedidosDaConta = pedidoRepository.findByIdConta(idConta);

        double total = 0.0;
        for (Pedido pedido : pedidosDaConta) {
            for (ProdutoPedido pp : pedido.getProdutos()) {
                // Caso não tenha o total calculado no ProdutoPedido, calcule:
                // total += pp.getQuantidade() * pp.getProduto().getPreco();
                total += pp.getTotal();
            }
        }

        conta.setStatus(StatusConta.FECHADA);
        conta.setDataFechamento(LocalDateTime.now());
        conta.setTotal(total);
        contaRepository.save(conta);

        // Atualiza status da mesa para DISPONIVEL
        Mesa mesa = mesaRepository.findById(conta.getIdMesa())
                .orElseThrow(() -> new IllegalArgumentException("Mesa não encontrada"));
        mesa.setStatus(StatusMesa.DISPONIVEL);
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
