package br.com.tregua.model;

public enum MotivoSilencio {

    BEBE("Bebê dormindo", 3),
    DOENTE("Pessoa doente", 3),
    PLANTAO("Descanso pós-plantão", 2),
    PROVA("Véspera de prova", 2),
    TRABALHO("Reunião / home office", 1),
    DESCANSO("Descanso", 1);

    private String descricao;
    private int peso;

    MotivoSilencio(String descricao, int peso) {
        this.descricao = descricao;
        this.peso = peso;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPeso() {
        return peso;
    }
}
