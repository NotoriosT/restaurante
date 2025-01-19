package com.tupa.restaurante.service;

import com.tupa.restaurante.dto.ChartDataDTO;
import com.tupa.restaurante.entities.Pedido;
import com.tupa.restaurante.entities.Produto;
import com.tupa.restaurante.entities.ProdutoPedido;
import com.tupa.restaurante.entities.enums.Status;
import com.tupa.restaurante.repository.PedidoRepository;
import com.tupa.restaurante.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;
    public ProdutoService(ProdutoRepository produtoRepository, PedidoRepository pedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public Produto save(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto findById(String id) {
        return produtoRepository.findById(id).orElse(null);
    }

    public void deleteById(String id) {
        produtoRepository.deleteById(id);
    }
    public ChartDataDTO getProdutosMaisVendidos(int top) {
        // Busca todos os pedidos, sem filtrar pelo status
        List<Pedido> pedidos = pedidoRepository.findAll();

        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
            return new ChartDataDTO(); // Retorna um objeto vazio.
        }

        // Mapeia os produtos e suas quantidades
        Map<String, Integer> produtoQuantidadeMap = new HashMap<>();

        for (Pedido pedido : pedidos) {
            for (ProdutoPedido produtoPedido : pedido.getProdutos()) {
                produtoQuantidadeMap.put(
                        produtoPedido.getIdProduto(),
                        produtoQuantidadeMap.getOrDefault(produtoPedido.getIdProduto(), 0) + produtoPedido.getQuantidade()
                );
            }
        }

        if (produtoQuantidadeMap.isEmpty()) {
            System.out.println("Nenhum produto relacionado aos pedidos encontrados.");
            return new ChartDataDTO();
        }

        // Ordena os produtos por quantidade e limita ao 'top'
        List<Map.Entry<String, Integer>> sortedProdutos = produtoQuantidadeMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(top)
                .collect(Collectors.toList());

        // Prepara os dados para o gráfico
        ChartDataDTO chartData = new ChartDataDTO();
        chartData.setLabels(new ArrayList<>());
        chartData.setDatasets(new ArrayList<>());

        ChartDataDTO.Dataset dataset = new ChartDataDTO.Dataset();
        dataset.setLabel("Produtos Mais Vendidos");
        dataset.setData(new ArrayList<>());
        dataset.setBackgroundColor(new ArrayList<>());

        Random random = new Random();

        for (Map.Entry<String, Integer> entry : sortedProdutos) {
            Optional<Produto> produtoOpt = produtoRepository.findById(entry.getKey());
            if (produtoOpt.isPresent()) {
                Produto produto = produtoOpt.get();
                chartData.getLabels().add(produto.getNome());
                dataset.getData().add(entry.getValue());
                dataset.getBackgroundColor().add(String.format("rgba(%d,%d,%d,0.7)", random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            } else {
                System.out.println("Produto com ID " + entry.getKey() + " não encontrado.");
            }
        }

        chartData.getDatasets().add(dataset);
        return chartData;
    }

}
