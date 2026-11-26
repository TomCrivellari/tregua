package br.com.tregua.model;

import java.util.ArrayList;
import java.util.List;

public class Bloco {

    private String nome;
    private List<Unidade> unidades = new ArrayList<>();

    public Bloco(String nome) {
        this.nome = nome;
    }

    public Unidade criarUnidade(int andar, int posicao) {
        Unidade unidade = new Unidade(this, andar, posicao);
        unidades.add(unidade);
        return unidade;
    }

    public String getNome() {
        return nome;
    }

    public List<Unidade> getUnidades() {
        return unidades;
    }
}
