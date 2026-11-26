package br.com.tregua.model;

import java.time.LocalDateTime;

public class Transacao {

    private int valor;
    private String descricao;
    private LocalDateTime data;

    public Transacao(int valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
        this.data = LocalDateTime.now();
    }

    public int getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getData() {
        return data;
    }
}
