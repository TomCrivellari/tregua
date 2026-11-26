package br.com.tregua.model;

public class Unidade {

    private Long id;
    private Bloco bloco;
    private int andar;
    private int posicao;
    private Morador morador;
    private Carteira carteira = new Carteira();

    public Unidade(Bloco bloco, int andar, int posicao) {
        this.bloco = bloco;
        this.andar = andar;
        this.posicao = posicao;
    }

    // ex: A-302 (bloco A, 3o andar, apto 2)
    public String getNome() {
        return bloco.getNome() + "-" + (andar * 100 + posicao);
    }

    // quantos aptos de distancia (andares + lados), se for de outro bloco nao conta
    public int distancia(Unidade outra) {
        if (outra.bloco != this.bloco) {
            return 99;
        }
        return Math.abs(andar - outra.andar) + Math.abs(posicao - outra.posicao);
    }

    public boolean vizinhaDireta(Unidade outra) {
        return distancia(outra) == 1;
    }

    // quanto mais alto o som, mais longe ele chega
    public boolean afetadaPor(Unidade origem, int decibeis) {
        int alcance = 1;
        if (decibeis >= 95) {
            alcance = 3;
        } else if (decibeis >= 80) {
            alcance = 2;
        }
        int d = distancia(origem);
        return d > 0 && d <= alcance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bloco getBloco() {
        return bloco;
    }

    public int getAndar() {
        return andar;
    }

    public int getPosicao() {
        return posicao;
    }

    public Morador getMorador() {
        return morador;
    }

    public void setMorador(Morador morador) {
        this.morador = morador;
    }

    public Carteira getCarteira() {
        return carteira;
    }
}
