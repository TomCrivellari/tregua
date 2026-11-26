package br.com.tregua.model;

import java.time.LocalDateTime;

public class PedidoSilencio {

    private Long id;
    private Unidade unidade;
    private MotivoSilencio motivo;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private String observacao;

    public PedidoSilencio(Unidade unidade, MotivoSilencio motivo, LocalDateTime inicio, LocalDateTime fim, String observacao) {
        this.unidade = unidade;
        this.motivo = motivo;
        this.inicio = inicio;
        this.fim = fim;
        this.observacao = observacao;
    }

    public boolean sobrepoe(LocalDateTime outroInicio, LocalDateTime outroFim) {
        return inicio.isBefore(outroFim) && outroInicio.isBefore(fim);
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

    public MotivoSilencio getMotivo() {
        return motivo;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public String getObservacao() {
        return observacao;
    }
}
