package com.tupa.restaurante.controller;

import com.tupa.restaurante.entidades.Pedido;
import com.tupa.restaurante.entidades.Status;
import com.tupa.restaurante.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

}
