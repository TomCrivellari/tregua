package br.com.tregua.service;

import br.com.tregua.model.Cotacao;
import br.com.tregua.model.Evento;
import br.com.tregua.model.PedidoSilencio;
import br.com.tregua.model.Regras;
import br.com.tregua.model.TipoEvento;
import br.com.tregua.model.Unidade;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CalculadoraCusto {

    private Feriados feriados;

    public CalculadoraCusto(Feriados feriados) {
        this.feriados = feriados;
    }

    public Cotacao calcular(Evento evento, Regras regras, List<PedidoSilencio> pedidos) {
        Cotacao cotacao = new Cotacao();
        String erro = validar(evento, regras);
        if (erro != null) {
            cotacao.setErro(erro);
            return cotacao;
        }

        LocalDateTime inicio = evento.getInicio();
        LocalDateTime fim = evento.getFim();
        int valorHora = evento.getTipo().getValorHora();

        int horas = (int) Math.ceil(evento.getDuracaoMinutos() / 60.0);
        int custo = horas * valorHora;
        cotacao.addDetalhe(horas + "h de " + evento.getTipo().getDescricao() + ": " + custo);

        // entrou no horario de silencio paga a hora em dobro
        long minutosSilencio = regras.minutosEmSilencio(inicio, fim);
        if (minutosSilencio > 0) {
            int horasSilencio = (int) Math.ceil(minutosSilencio / 60.0);
            custo += horasSilencio * valorHora;
            cotacao.addDetalhe("Horário de silêncio: +" + horasSilencio * valorHora);
        }

        // cada 5 dB acima do limite custa 2 creditos
        int limite = minutosSilencio > 0 ? regras.getLimiteNoite() : regras.getLimiteDia();
        if (evento.getDecibeis() > limite) {
            int faixas = (int) Math.ceil((evento.getDecibeis() - limite) / 5.0);
            custo += faixas * 2;
            cotacao.addDetalhe("Acima de " + limite + " dB: +" + faixas * 2);
        }

        if (feriados.isFeriado(inicio.toLocalDate())) {
            cotacao.addDetalhe("Feriado: +" + custo / 2);
            custo += custo / 2;
        } else if (fimDeSemana(inicio)) {
            cotacao.addDetalhe("Fim de semana: +" + custo / 5);
            custo += custo / 5;
        }

        // vizinho que pediu silencio no mesmo horario deixa o evento mais caro
        Unidade organizador = evento.getUnidade();
        int pontos = 0;
        for (PedidoSilencio pedido : pedidos) {
            if (pedido.getUnidade() == organizador || !pedido.sobrepoe(inicio, fim)) {
                continue;
            }
            if (pedido.getUnidade().vizinhaDireta(organizador)) {
                pontos += pedido.getMotivo().getPeso() * 2;
            } else if (pedido.getUnidade().afetadaPor(organizador, evento.getDecibeis())) {
                pontos += pedido.getMotivo().getPeso();
            }
        }
        if (pontos > 0) {
            custo += pontos * 3;
            cotacao.addDetalhe("Pedidos de silêncio dos vizinhos: +" + pontos * 3);
        }

        // avisou em cima da hora
        if (Duration.between(LocalDateTime.now(), inicio).toHours() < 24) {
            cotacao.addDetalhe("Menos de 24h de antecedência: +" + custo / 2);
            custo += custo / 2;
        }

        cotacao.setValor(custo);
        return cotacao;
    }

    private String validar(Evento evento, Regras regras) {
        LocalDateTime inicio = evento.getInicio();
        LocalDateTime fim = evento.getFim();
        if (!fim.isAfter(inicio)) {
            return "O fim tem que ser depois do início";
        }
        if (inicio.isBefore(LocalDateTime.now())) {
            return "Não dá para agendar evento no passado";
        }
        if (evento.getDuracaoMinutos() > regras.getDuracaoMaxima()) {
            return "O evento pode ter no máximo " + regras.getDuracaoMaxima() / 60 + " horas";
        }
        if (evento.getDecibeis() < 40 || evento.getDecibeis() > regras.getLimiteMaximo()) {
            return "O volume tem que ficar entre 40 e " + regras.getLimiteMaximo() + " dB";
        }
        if (evento.getTipo() == TipoEvento.REFORMA) {
            boolean domingoOuFeriado = inicio.getDayOfWeek() == DayOfWeek.SUNDAY || feriados.isFeriado(inicio.toLocalDate());
            if (domingoOuFeriado || regras.minutosEmSilencio(inicio, fim) > 0) {
                return "Reforma não pode em domingo, feriado ou horário de silêncio";
            }
        }
        return null;
    }

    private boolean fimDeSemana(LocalDateTime data) {
        DayOfWeek dia = data.getDayOfWeek();
        return dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY;
    }
}
