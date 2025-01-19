package com.tupa.restaurante.exceptions;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String mensagem;
    private LocalDateTime dataHora;

    public ErrorResponse(int status, String mensagem, LocalDateTime dataHora) {
        this.status = status;
        this.mensagem = mensagem;
        this.dataHora = dataHora;
    }

    // Getters
    public int getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
