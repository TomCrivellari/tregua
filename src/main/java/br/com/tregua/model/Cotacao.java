package br.com.tregua.model;

import java.util.ArrayList;
import java.util.List;

public class Cotacao {

    private int valor;
    private String erro;
    private List<String> detalhes = new ArrayList<>();

    public void addDetalhe(String detalhe) {
        detalhes.add(detalhe);
    }

    public boolean isBloqueada() {
        return erro != null;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public List<String> getDetalhes() {
        return detalhes;
    }
}
