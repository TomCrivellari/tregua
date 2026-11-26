package br.com.tregua.model;

import java.util.ArrayList;
import java.util.List;

public class Condominio {

    private String nome;
    private Regras regras;
    private List<Bloco> blocos = new ArrayList<>();
    private int fundo;

    public Condominio(String nome, Regras regras) {
        this.nome = nome;
        this.regras = regras;
    }

    public void addBloco(Bloco bloco) {
        blocos.add(bloco);
    }

    public List<Unidade> getUnidades() {
        List<Unidade> unidades = new ArrayList<>();
        for (Bloco bloco : blocos) {
            unidades.addAll(bloco.getUnidades());
        }
        return unidades;
    }

    public void adicionarAoFundo(int valor) {
        fundo += valor;
    }

    public String getNome() {
        return nome;
    }

    public Regras getRegras() {
        return regras;
    }

    public List<Bloco> getBlocos() {
        return blocos;
    }

    public int getFundo() {
        return fundo;
    }
}
