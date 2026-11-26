package br.com.tregua.model;

import java.time.LocalDateTime;

public class Reclamacao {

    private Long id;
    private Morador autor;
    private Unidade alvo;
    private LocalDateTime dataHora;
    private String descricao;
    private StatusReclamacao status = StatusReclamacao.ABERTA;
    private int multa;
    private String parecer;

    public Reclamacao(Morador autor, Unidade alvo, LocalDateTime dataHora, String descricao) {
        this.autor = autor;
        this.alvo = alvo;
        this.dataHora = dataHora;
        this.descricao = descricao;
    }

    public void fechar(StatusReclamacao status, int multa, String parecer) {
        this.status = status;
        this.multa = multa;
        this.parecer = parecer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Morador getAutor() {
        return autor;
    }

    public Unidade getAlvo() {
        return alvo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusReclamacao getStatus() {
        return status;
    }

    public int getMulta() {
        return multa;
    }

    public String getParecer() {
        return parecer;
    }
}
