package br.com.tregua.service;

import br.com.tregua.model.Medicao;
import br.com.tregua.model.Morador;
import br.com.tregua.model.NegocioException;
import br.com.tregua.model.Reclamacao;
import br.com.tregua.model.StatusReclamacao;
import br.com.tregua.model.Unidade;
import br.com.tregua.repository.BancoDeDados;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReclamacaoService {

    private BancoDeDados banco;
    private AvaliadorReclamacao avaliador;

    public ReclamacaoService(BancoDeDados banco, AvaliadorReclamacao avaliador) {
        this.banco = banco;
        this.avaliador = avaliador;
    }

    public Reclamacao reclamar(Long autorId, Long alvoId, LocalDateTime dataHora, String descricao, Integer decibeis) {
        if (dataHora.isAfter(LocalDateTime.now())) {
            throw new NegocioException("Não dá para reclamar de algo que ainda não aconteceu");
        }
        Morador autor = banco.buscarMorador(autorId);
        Unidade alvo = banco.buscarUnidade(alvoId);

        if (decibeis != null) {
            banco.getMedicoes().add(new Medicao(alvo, decibeis, dataHora));
        }

        Reclamacao reclamacao = new Reclamacao(autor, alvo, dataHora, descricao);
        avaliador.julgar(reclamacao, banco.getMedicoes(), banco.getEventos(), contarReincidencias(alvo, dataHora),
                banco.getCondominio().getRegras());
        reclamacao.setId(banco.novoId());
        banco.getReclamacoes().add(reclamacao);

        // a multa vai para quem reclamou
        if (reclamacao.getStatus() == StatusReclamacao.PROCEDENTE) {
            int valor = Math.min(reclamacao.getMulta(), alvo.getCarteira().getSaldo());
            if (valor > 0) {
                alvo.getCarteira().debitar(valor, "Multa por barulho");
                autor.getUnidade().getCarteira().creditar(valor, "Multa paga por " + alvo.getNome());
            }
        }
        return reclamacao;
    }

    private int contarReincidencias(Unidade alvo, LocalDateTime dataHora) {
        int total = 0;
        for (Reclamacao r : banco.getReclamacoes()) {
            if (r.getAlvo() == alvo && r.getStatus() == StatusReclamacao.PROCEDENTE
                    && r.getDataHora().isAfter(dataHora.minusDays(30))) {
                total++;
            }
        }
        return total;
    }

    public List<Reclamacao> listar() {
        return banco.getReclamacoes();
    }
}
