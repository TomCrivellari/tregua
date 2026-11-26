package br.com.tregua.service;

import br.com.tregua.model.Condominio;
import br.com.tregua.model.Cotacao;
import br.com.tregua.model.Evento;
import br.com.tregua.model.NegocioException;
import br.com.tregua.model.TipoEvento;
import br.com.tregua.model.Unidade;
import br.com.tregua.repository.BancoDeDados;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventoService {

    private BancoDeDados banco;
    private CalculadoraCusto calculadora;

    public EventoService(BancoDeDados banco, CalculadoraCusto calculadora) {
        this.banco = banco;
        this.calculadora = calculadora;
    }

    public Evento agendar(Long unidadeId, TipoEvento tipo, String descricao, LocalDateTime inicio, LocalDateTime fim, int decibeis) {
        Unidade unidade = banco.buscarUnidade(unidadeId);
        Evento evento = new Evento(unidade, tipo, descricao, inicio, fim, decibeis);

        Condominio condominio = banco.getCondominio();
        Cotacao cotacao = calculadora.calcular(evento, condominio.getRegras(), banco.getPedidos());
        if (cotacao.isBloqueada()) {
            throw new NegocioException(cotacao.getErro());
        }

        unidade.getCarteira().debitar(cotacao.getValor(), "Evento: " + descricao);
        evento.setCusto(cotacao.getValor());
        evento.setId(banco.novoId());
        banco.getEventos().add(evento);

        repassarParaVizinhos(evento, condominio);
        return evento;
    }

    // o valor pago vai para os vizinhos que vao ouvir o barulho, uma parte fica no fundo
    private void repassarParaVizinhos(Evento evento, Condominio condominio) {
        List<Unidade> afetadas = new ArrayList<>();
        for (Unidade u : condominio.getUnidades()) {
            if (u.getMorador() != null && u.afetadaPor(evento.getUnidade(), evento.getDecibeis())) {
                afetadas.add(u);
            }
        }

        int fundo = evento.getCusto() * condominio.getRegras().getPercentualFundo() / 100;
        int resto = evento.getCusto() - fundo;
        if (afetadas.isEmpty()) {
            condominio.adicionarAoFundo(evento.getCusto());
            return;
        }

        int parte = resto / afetadas.size();
        if (parte > 0) {
            for (Unidade u : afetadas) {
                u.getCarteira().creditar(parte, "Compensação: " + evento.getDescricao());
            }
        }
        condominio.adicionarAoFundo(evento.getCusto() - parte * afetadas.size());
    }

    public void cancelar(Long id) {
        Evento evento = banco.buscarEvento(id);
        if (evento.isCancelado()) {
            throw new NegocioException("Evento já está cancelado");
        }
        // os creditos nao voltam, os vizinhos ja receberam
        evento.setCancelado(true);
    }

    public List<Evento> listar() {
        return banco.getEventos();
    }
}
