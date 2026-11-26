package br.com.tregua.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Evento {

    private Long id;
    private Unidade unidade;
    private TipoEvento tipo;
    private String descricao;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private int decibeis;
    private int custo;
    private boolean cancelado;

    public Evento(Unidade unidade, TipoEvento tipo, String descricao, LocalDateTime inicio, LocalDateTime fim, int decibeis) {
        this.unidade = unidade;
        this.tipo = tipo;
        this.descricao = descricao;
        this.inicio = inicio;
        this.fim = fim;
        this.decibeis = decibeis;
    }

    public long getDuracaoMinutos() {
        return Duration.between(inicio, fim).toMinutes();
    }

    public boolean sobrepoe(LocalDateTime outroInicio, LocalDateTime outroFim) {
        return inicio.isBefore(outroFim) && outroInicio.isBefore(fim);
    }

    public boolean acontecendoEm(LocalDateTime dataHora) {
        return !dataHora.isBefore(inicio) && dataHora.isBefore(fim);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public int getDecibeis() {
        return decibeis;
    }

    public int getCusto() {
        return custo;
    }

    public void setCusto(int custo) {
        this.custo = custo;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }
}
