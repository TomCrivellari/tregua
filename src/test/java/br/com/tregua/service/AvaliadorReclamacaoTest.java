package br.com.tregua.service;

import br.com.tregua.model.Bloco;
import br.com.tregua.model.Evento;
import br.com.tregua.model.Medicao;
import br.com.tregua.model.Morador;
import br.com.tregua.model.Reclamacao;
import br.com.tregua.model.Regras;
import br.com.tregua.model.StatusReclamacao;
import br.com.tregua.model.TipoEvento;
import br.com.tregua.model.Unidade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvaliadorReclamacaoTest {

    private static final LocalDateTime NOITE = LocalDateTime.of(2030, 1, 8, 23, 0);
    private static final LocalDateTime TARDE = LocalDateTime.of(2030, 1, 8, 15, 0);

    private AvaliadorReclamacao avaliador;
    private Regras regras;
    private List<Medicao> medicoes;
    private List<Evento> eventos;
    private Unidade alvo;
    private Unidade outra;
    private Morador autor;

    @BeforeEach
    void preparar() {
        avaliador = new AvaliadorReclamacao();
        regras = new Regras();
        medicoes = new ArrayList<>();
        eventos = new ArrayList<>();
        Bloco bloco = new Bloco("A");
        alvo = bloco.criarUnidade(2, 1);
        outra = bloco.criarUnidade(2, 2);
        autor = new Morador("Caio", "caio@email.com", bloco.criarUnidade(1, 1));
    }

    private Reclamacao julgar(LocalDateTime quando, int reincidencias) {
        Reclamacao r = new Reclamacao(autor, alvo, quando, "barulho");
        avaliador.julgar(r, medicoes, eventos, reincidencias, regras);
        return r;
    }

    private void medir(Unidade unidade, int decibeis, LocalDateTime quando) {
        medicoes.add(new Medicao(unidade, decibeis, quando));
    }

    @Test
    void propriaUnidade() {
        Reclamacao r = new Reclamacao(autor, autor.getUnidade(), NOITE, "eu mesmo");
        avaliador.julgar(r, medicoes, eventos, 0, regras);
        assertEquals(StatusReclamacao.IMPROCEDENTE, r.getStatus());
        assertEquals("Não pode reclamar da própria unidade", r.getParecer());
    }

    @Test
    void semMedicao() {
        assertEquals(StatusReclamacao.INCONCLUSIVA, julgar(NOITE, 0).getStatus());
    }

    @Test
    void medicaoOutraUnidade() {
        medir(outra, 90, NOITE);
        assertEquals(StatusReclamacao.INCONCLUSIVA, julgar(NOITE, 0).getStatus());
    }

    @Test
    void medicao21Minutos() {
        medir(alvo, 90, NOITE.minusMinutes(21));
        assertEquals(StatusReclamacao.INCONCLUSIVA, julgar(NOITE, 0).getStatus());
    }

    @Test
    void medicao20Minutos() {
        medir(alvo, 90, NOITE.plusMinutes(20));
        assertEquals(StatusReclamacao.PROCEDENTE, julgar(NOITE, 0).getStatus());
    }

    @Test
    void pegaMaiorMedicao() {
        medir(alvo, 60, NOITE);
        medir(alvo, 52, NOITE.plusMinutes(5));
        Reclamacao r = julgar(NOITE, 0);
        // pico 60, limite 50, excesso 10
        assertEquals(20, r.getMulta());
    }

    @Test
    void noite50dB() {
        medir(alvo, 50, NOITE);
        Reclamacao r = julgar(NOITE, 0);
        assertEquals(StatusReclamacao.IMPROCEDENTE, r.getStatus());
        assertEquals(0, r.getMulta());
    }

    @Test
    void noite51dB() {
        medir(alvo, 51, NOITE);
        Reclamacao r = julgar(NOITE, 0);
        assertEquals(StatusReclamacao.PROCEDENTE, r.getStatus());
        assertEquals(10, r.getMulta());
    }

    @Test
    void tarde65dB() {
        medir(alvo, 65, TARDE);
        assertEquals(StatusReclamacao.IMPROCEDENTE, julgar(TARDE, 0).getStatus());
    }

    @Test
    void tarde66dB() {
        medir(alvo, 66, TARDE);
        assertEquals(StatusReclamacao.PROCEDENTE, julgar(TARDE, 0).getStatus());
    }

    @Test
    void excesso5() {
        medir(alvo, 55, NOITE);
        assertEquals(10, julgar(NOITE, 0).getMulta());
    }

    @Test
    void excesso6() {
        medir(alvo, 56, NOITE);
        assertEquals(20, julgar(NOITE, 0).getMulta());
    }

    @Test
    void excesso15() {
        medir(alvo, 65, NOITE);
        assertEquals(20, julgar(NOITE, 0).getMulta());
    }

    @Test
    void excesso16() {
        medir(alvo, 66, NOITE);
        assertEquals(35, julgar(NOITE, 0).getMulta());
    }

    @Test
    void umaReincidencia() {
        medir(alvo, 55, NOITE);
        assertEquals(10, julgar(NOITE, 1).getMulta());
    }

    @Test
    void duasReincidencias() {
        medir(alvo, 55, NOITE);
        assertEquals(20, julgar(NOITE, 2).getMulta());
    }

    @Test
    void eventoCobre83dB() {
        eventos.add(new Evento(alvo, TipoEvento.FESTA, "Aniversário", NOITE.minusHours(1), NOITE.plusHours(1), 80));
        medir(alvo, 83, NOITE);
        Reclamacao r = julgar(NOITE, 0);
        assertEquals(StatusReclamacao.IMPROCEDENTE, r.getStatus());
        assertEquals("Barulho coberto pelo evento Aniversário", r.getParecer());
    }

    @Test
    void eventoNaoCobre84dB() {
        eventos.add(new Evento(alvo, TipoEvento.FESTA, "Aniversário", NOITE.minusHours(1), NOITE.plusHours(1), 80));
        medir(alvo, 84, NOITE);
        Reclamacao r = julgar(NOITE, 0);
        // limite passa a ser o volume do evento (80), excesso 4
        assertEquals(StatusReclamacao.PROCEDENTE, r.getStatus());
        assertEquals(10, r.getMulta());
    }

    @Test
    void eventosIgnorados() {
        Evento deOutraUnidade = new Evento(outra, TipoEvento.FESTA, "outra", NOITE.minusHours(1), NOITE.plusHours(1), 90);
        Evento cancelado = new Evento(alvo, TipoEvento.FESTA, "cancelado", NOITE.minusHours(1), NOITE.plusHours(1), 90);
        cancelado.setCancelado(true);
        Evento outroHorario = new Evento(alvo, TipoEvento.FESTA, "ontem", NOITE.minusDays(1), NOITE.minusDays(1).plusHours(2), 90);
        eventos.add(deOutraUnidade);
        eventos.add(cancelado);
        eventos.add(outroHorario);
        medir(alvo, 70, NOITE);

        Reclamacao r = julgar(NOITE, 0);
        assertEquals(StatusReclamacao.PROCEDENTE, r.getStatus());
        assertEquals(35, r.getMulta());
    }
}
