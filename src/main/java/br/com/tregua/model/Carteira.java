package br.com.tregua.model;

import java.util.ArrayList;
import java.util.List;

public class Carteira {

    private int saldo;
    private List<Transacao> extrato = new ArrayList<>();

    public void creditar(int valor, String descricao) {
        if (valor <= 0) {
            throw new NegocioException("Valor inválido");
        }
        saldo += valor;
        extrato.add(new Transacao(valor, descricao));
    }

    public void debitar(int valor, String descricao) {
        if (valor <= 0) {
            throw new NegocioException("Valor inválido");
        }
        if (valor > saldo) {
            throw new NegocioException("Saldo insuficiente: você tem " + saldo + " créditos e precisa de " + valor);
        }
        saldo -= valor;
        extrato.add(new Transacao(-valor, descricao));
    }

    public int getSaldo() {
        return saldo;
    }

    public List<Transacao> getExtrato() {
        return extrato;
    }
}
