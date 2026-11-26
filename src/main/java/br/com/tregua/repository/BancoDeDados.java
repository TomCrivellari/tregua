package br.com.tregua.repository;

import br.com.tregua.model.Condominio;
import br.com.tregua.model.Evento;
import br.com.tregua.model.Medicao;
import br.com.tregua.model.Morador;
import br.com.tregua.model.NegocioException;
import br.com.tregua.model.PedidoSilencio;
import br.com.tregua.model.Reclamacao;
import br.com.tregua.model.Unidade;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// guarda tudo em memoria, os dados somem quando reinicia
@Component
public class BancoDeDados {

    private Condominio condominio;
    private List<Morador> moradores = new ArrayList<>();
    private List<Evento> eventos = new ArrayList<>();
    private List<PedidoSilencio> pedidos = new ArrayList<>();
    private List<Reclamacao> reclamacoes = new ArrayList<>();
    private List<Medicao> medicoes = new ArrayList<>();
    private long ultimoId = 0;

    public synchronized Long novoId() {
        ultimoId++;
        return ultimoId;
    }

    public Unidade buscarUnidade(Long id) {
        for (Unidade u : condominio.getUnidades()) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        throw new NegocioException("Unidade não encontrada");
    }

    public Morador buscarMorador(Long id) {
        for (Morador m : moradores) {
            if (m.getId().equals(id)) {
                return m;
            }
        }
        throw new NegocioException("Morador não encontrado");
    }

    public Evento buscarEvento(Long id) {
        for (Evento e : eventos) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        throw new NegocioException("Evento não encontrado");
    }

    public Condominio getCondominio() {
        return condominio;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public List<Morador> getMoradores() {
        return moradores;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public List<PedidoSilencio> getPedidos() {
        return pedidos;
    }

    public List<Reclamacao> getReclamacoes() {
        return reclamacoes;
    }

    public List<Medicao> getMedicoes() {
        return medicoes;
    }
}
