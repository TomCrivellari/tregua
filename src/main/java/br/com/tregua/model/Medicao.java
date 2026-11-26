package br.com.tregua.model;

import java.time.LocalDateTime;

// leitura de decibeis feita pelo sensor do andar ou pelo app do morador
public class Medicao {

    private Unidade unidade;
    private int decibeis;
    private LocalDateTime dataHora;

    public Medicao(Unidade unidade, int decibeis, LocalDateTime dataHora) {
        this.unidade = unidade;
        this.decibeis = decibeis;
        this.dataHora = dataHora;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public int getDecibeis() {
        return decibeis;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
