package br.com.tregua.model;

public enum TipoEvento {

    FESTA("Festa", 8),
    REFORMA("Reforma", 6),
    ENSAIO("Ensaio musical", 5),
    MUDANCA("Mudança", 4);

    private String descricao;
    private int valorHora;

    TipoEvento(String descricao, int valorHora) {
        this.descricao = descricao;
        this.valorHora = valorHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getValorHora() {
        return valorHora;
    }
}
