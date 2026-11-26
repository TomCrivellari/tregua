package br.com.tregua.service;

import br.com.tregua.model.Evento;
import br.com.tregua.model.Medicao;
import br.com.tregua.model.Reclamacao;
import br.com.tregua.model.Regras;
import br.com.tregua.model.StatusReclamacao;
import br.com.tregua.model.Unidade;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AvaliadorReclamacao {

    public void julgar(Reclamacao reclamacao, List<Medicao> medicoes, List<Evento> eventos, int reincidencias, Regras regras) {
        Unidade alvo = reclamacao.getAlvo();
        LocalDateTime quando = reclamacao.getDataHora();

        if (alvo == reclamacao.getAutor().getUnidade()) {
            reclamacao.fechar(StatusReclamacao.IMPROCEDENTE, 0, "Não pode reclamar da própria unidade");
            return;
        }

        // pega a maior medicao do alvo ate 20 min antes ou depois
        int pico = -1;
        for (Medicao m : medicoes) {
            long minutos = Math.abs(Duration.between(m.getDataHora(), quando).toMinutes());
            if (m.getUnidade() == alvo && minutos <= 20 && m.getDecibeis() > pico) {
                pico = m.getDecibeis();
            }
        }
        if (pico == -1) {
            reclamacao.fechar(StatusReclamacao.INCONCLUSIVA, 0, "Nenhuma medição perto desse horário");
            return;
        }

        // se tinha evento pago na hora, vale o volume combinado no evento
        int limite = regras.limiteEm(quando);
        for (Evento e : eventos) {
            if (e.getUnidade() == alvo && !e.isCancelado() && e.acontecendoEm(quando)) {
                if (pico <= e.getDecibeis() + 3) {
                    reclamacao.fechar(StatusReclamacao.IMPROCEDENTE, 0, "Barulho coberto pelo evento " + e.getDescricao());
                    return;
                }
                limite = e.getDecibeis();
            }
        }

        if (pico <= limite) {
            reclamacao.fechar(StatusReclamacao.IMPROCEDENTE, 0, pico + " dB está dentro do limite de " + limite + " dB");
            return;
        }

        int excesso = pico - limite;
        int multa;
        if (excesso <= 5) {
            multa = 10;
        } else if (excesso <= 15) {
            multa = 20;
        } else {
            multa = 35;
        }
        if (reincidencias >= 2) {
            multa = multa * 2;
        }
        reclamacao.fechar(StatusReclamacao.PROCEDENTE, multa, pico + " dB, " + excesso + " dB acima do limite");
    }
}
