package com.tupa.restaurante.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ChartDataDTO {
    private List<String> labels;
    private List<Dataset> datasets;

    // Getters e Setters


    @Setter
    @Getter
    public static class Dataset {
        private String label;
        private List<Number> data;
        private List<String> backgroundColor; // Cores de fundo
        private List<String> borderColor;    // Cores da borda (opcional)
    }
}
