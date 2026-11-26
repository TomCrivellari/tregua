package br.com.tregua.service;

import br.com.tregua.model.Bloco;
import br.com.tregua.model.Cotacao;
import br.com.tregua.model.Evento;
import br.com.tregua.model.Regras;
import br.com.tregua.model.TipoEvento;
import br.com.tregua.model.Unidade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculadoraCustoMockTest {

    @Mock
    private Feriados feriados;

    @Mock
    private Relogio relogio;

    private CalculadoraCusto calculadora;
    private Unidade unidade;

    // terca-feira comum, sem feriado de verdade
    private final LocalDateTime inicio = LocalDateTime.of(2030, 1, 8, 14, 0);

    @BeforeEach
    void preparar() {
        calculadora = new CalculadoraCusto(feriados, relogio);
        unidade = new Bloco("A").criarUnidade(2, 1);
    }

    private Cotacao calcular(TipoEvento tipo) {
        Evento evento = new Evento(unidade, tipo, "teste", inicio, inicio.plusHours(1), 60);
        return calculadora.calcular(evento, new Regras(), List.of());
    }

    @Test
    void feriadoMockado() {
        when(relogio.agora()).thenReturn(LocalDateTime.of(2030, 1, 1, 10, 0));
        when(feriados.isFeriado(any())).thenReturn(true);

        Cotacao c = calcular(TipoEvento.FESTA);

        assertEquals(12, c.getValor());
        verify(feriados).isFeriado(LocalDate.of(2030, 1, 8));
    }

    @Test
    void urgenciaComRelogioMockado() {
        when(relogio.agora()).thenReturn(inicio.minusHours(2));
        when(feriados.isFeriado(any())).thenReturn(false);

        Cotacao c = calcular(TipoEvento.FESTA);

        assertEquals(12, c.getValor());
        assertTrue(c.getDetalhes().contains("Menos de 24h de antecedência: +4"));
        verify(relogio, times(2)).agora();
    }

    @Test
    void passadoComRelogioMockado() {
        when(relogio.agora()).thenReturn(inicio.plusMinutes(1));

        Cotacao c = calcular(TipoEvento.FESTA);

        assertEquals("Não dá para agendar evento no passado", c.getErro());
        verify(feriados, never()).isFeriado(any());
    }

    @Test
    void reformaEmFeriadoMockado() {
        when(relogio.agora()).thenReturn(LocalDateTime.of(2030, 1, 1, 10, 0));
        when(feriados.isFeriado(any())).thenReturn(true);

        Cotacao c = calcular(TipoEvento.REFORMA);

        assertEquals("Reforma não pode em domingo, feriado ou horário de silêncio", c.getErro());
    }
}
