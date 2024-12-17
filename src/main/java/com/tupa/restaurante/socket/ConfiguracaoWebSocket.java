package com.tupa.restaurante.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class ConfiguracaoWebSocket implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Define os destinos para mensagens enviadas aos clientes
        registry.enableSimpleBroker("/topico", "/fila");
        // Define o prefixo para mensagens que serão enviadas do cliente para o servidor
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Define o endpoint WebSocket que os clientes vão se conectar
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
