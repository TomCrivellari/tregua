package br.com.tregua.model;

public class Morador {

    private Long id;
    private String nome;
    private String email;
    private Unidade unidade;

    public Morador(String nome, String email, Unidade unidade) {
        this.nome = nome;
        this.email = email;
        this.unidade = unidade;
        unidade.setMorador(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Unidade getUnidade() {
        return unidade;
    }
}
