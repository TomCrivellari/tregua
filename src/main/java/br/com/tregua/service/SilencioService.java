package br.com.tregua.service;

import br.com.tregua.model.MotivoSilencio;
import br.com.tregua.model.NegocioException;
import br.com.tregua.model.PedidoSilencio;
import br.com.tregua.model.Unidade;
import br.com.tregua.repository.BancoDeDados;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SilencioService {

    private BancoDeDados banco;

    public SilencioService(BancoDeDados banco) {
        this.banco = banco;
    }

    public PedidoSilencio pedir(Long unidadeId, MotivoSilencio motivo, LocalDateTime inicio, LocalDateTime fim, String observacao) {
        if (!fim.isAfter(inicio)) {
            throw new NegocioException("O fim tem que ser depois do início");
        }
        if (fim.isBefore(LocalDateTime.now())) {
            throw new NegocioException("Esse horário já passou");
        }
        if (Duration.between(inicio, fim).toHours() > 12) {
            throw new NegocioException("O pedido de silêncio pode ter no máximo 12 horas");
        }
        Unidade unidade = banco.buscarUnidade(unidadeId);
        PedidoSilencio pedido = new PedidoSilencio(unidade, motivo, inicio, fim, observacao);
        pedido.setId(banco.novoId());
        banco.getPedidos().add(pedido);
        return pedido;
    }

    public List<PedidoSilencio> listar() {
        return banco.getPedidos();
    }
}
