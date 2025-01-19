package com.tupa.restaurante.controller;

import com.tupa.restaurante.dto.ChartDataDTO;
import com.tupa.restaurante.service.PedidoService;
import com.tupa.restaurante.service.ProdutoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/graficos")
public class DetailController {

    private final PedidoService pedidoService;
    private final ProdutoService produtoService;

    public DetailController(PedidoService pedidoService, ProdutoService produtoService) {
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
    }

    /**
     * Endpoint para obter vendas por dia.
     *
     * @param startData Data de início (formato yyyy-MM-dd).
     * @param endData   Data de fim (formato yyyy-MM-dd).
     * @return Dados do gráfico de vendas por dia.
     */
    @GetMapping("/vendas-por-dia")
    public ResponseEntity<ChartDataDTO> getVendasPorDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startData,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endData) {
        ChartDataDTO chartData = pedidoService.getVendasPorDia(startData, endData);
        return ResponseEntity.ok(chartData);
    }

    /**
     * Endpoint para obter os produtos mais vendidos.
     *
     * @param top Quantidade de produtos a retornar.
     * @return Dados do gráfico de produtos mais vendidos.
     */
    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<ChartDataDTO> getProdutosMaisVendidos(@RequestParam(defaultValue = "5") int top) {
        ChartDataDTO chartData = produtoService.getProdutosMaisVendidos(top);
        return ResponseEntity.ok(chartData);
    }

    /**
     * Endpoint para obter a distribuição de pedidos por status.
     *
     * @return Dados do gráfico de pedidos por status.
     */
    @GetMapping("/pedidos-por-status")
    public ResponseEntity<ChartDataDTO> getPedidosPorStatus() {
        ChartDataDTO chartData = pedidoService.getPedidosPorStatus();
        return ResponseEntity.ok(chartData);
    }

    // Você pode adicionar mais endpoints para outros tipos de gráficos conforme necessário
}
