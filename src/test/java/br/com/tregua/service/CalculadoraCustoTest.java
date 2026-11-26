package br.com.tregua.service;

import br.com.tregua.model.Bloco;
import br.com.tregua.model.Cotacao;
import br.com.tregua.model.Evento;
import br.com.tregua.model.MotivoSilencio;
import br.com.tregua.model.PedidoSilencio;
import br.com.tregua.model.Regras;
import br.com.tregua.model.TipoEvento;
import br.com.tregua.model.Unidade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculadoraCustoTest {

    // "agora" fixo: quarta 02/01/2030 10:00 (dia 1 é feriado, por isso dia 2)
    private static final LocalDateTime AGORA = LocalDateTime.of(2030, 1, 2, 10, 0);

    private CalculadoraCusto calculadora;
    private Regras regras;
    private List<PedidoSilencio> pedidos;
    private Unidade a201;
    private Unidade a301;
    private Unidade a302;
    private Unidade a402;

    @BeforeEach
    void preparar() {
        calculadora = new CalculadoraCusto(new Feriados(), () -> AGORA);
        regras = new Regras();
        pedidos = new ArrayList<>();
        Bloco bloco = new Bloco("A");
        a201 = bloco.criarUnidade(2, 1);
        a301 = bloco.criarUnidade(3, 1);
        a302 = bloco.criarUnidade(3, 2);
        a402 = bloco.criarUnidade(4, 2);
    }

    private Cotacao calcular(TipoEvento tipo, LocalDateTime inicio, LocalDateTime fim, int decibeis) {
        Evento evento = new Evento(a201, tipo, "teste", inicio, fim, decibeis);
        return calculadora.calcular(evento, regras, pedidos);
    }

    private LocalDateTime terca(int hora, int minuto) {
        return LocalDateTime.of(2030, 1, 8, hora, minuto);
    }

    // ---------- validações

    @Test
    void fimIgualAoInicioBloqueia() {
        Cotacao c = calcular(TipoEvento.FESTA, terca(14, 0), terca(14, 0), 60);
        assertEquals("O fim tem que ser depois do início", c.getErro());
    }

    @Test
    void fimUmMinutoDepoisDoInicioPassa() {
        Cotacao c = calcular(TipoEvento.FESTA, terca(14, 0), terca(14, 1), 60);
        assertFalse(c.isBloqueada());
        assertEquals(8, c.getValor());
    }

    @Test
    void inicioUmMinutoAntesDeAgoraBloqueia() {
        Cotacao c = calcular(TipoEvento.FESTA, AGORA.minusMinutes(1), AGORA.plusHours(1), 60);
        assertEquals("Não dá para agendar evento no passado", c.getErro());
    }

    @Test
    void inicioExatamenteAgoraPassaComUrgencia() {
        Cotacao c = calcular(TipoEvento.FESTA, AGORA, AGORA.plusHours(1), 60);
        assertFalse(c.isBloqueada());
        assertEquals(12, c.getValor());
    }

    @Test
    void duracaoDe480MinutosPassa() {
        Cotacao c = calcular(TipoEvento.FESTA, terca(10, 0), terca(18, 0), 60);
        assertFalse(c.isBloqueada());
        assertEquals(64, c.getValor());
    }

    @Test
    void duracaoDe481MinutosBloqueia() {
        Cotacao c = calcular(TipoEvento.FESTA, terca(10, 0), terca(18, 1), 60);
        assertEquals("O evento pode ter no máximo 8 horas", c.getErro());
    }

    @Test
    void volume39BloqueiaE40Passa() {
        assertTrue(calcular(TipoEvento.FESTA, terca(14, 0), terca(15, 0), 39).isBloqueada());
        assertFalse(calcular(TipoEvento.FESTA, terca(14, 0), terca(15, 0), 40).isBloqueada());
    }

    @Test
    void volume105PassaE106Bloqueia() {
        assertFalse(calcular(TipoEvento.FESTA, terca(14, 0), terca(15, 0), 105).isBloqueada());
        Cotacao c = calcular(TipoEvento.FESTA, terca(14, 0), terca(15, 0), 106);
        assertEquals("O volume tem que ficar entre 40 e 105 dB", c.getErro());
    }

    @Test
    void reformaNoDomingoBloqueia() {
        LocalDateTime domingo = LocalDateTime.of(2030, 1, 6, 9, 0);
        Cotacao c = calcular(TipoEvento.REFORMA, domingo, domingo.plusHours(2), 70);
        assertEquals("Reforma não pode em domingo, feriado ou horário de silêncio", c.getErro());
    }

    @Test
    void reformaNoFeriadoBloqueia() {
        LocalDateTime natal = LocalDateTime.of(2030, 12, 25, 9, 0);
        assertTrue(calcular(TipoEvento.REFORMA, natal, natal.plusHours(2), 70).isBloqueada());
    }

    @Test
    void reformaQueEntraUmMinutoNoSilencioBloqueia() {
        assertTrue(calcular(TipoEvento.REFORMA, terca(20, 0), terca(22, 1), 70).isBloqueada());
    }

    @Test
    void reformaQueTerminaAs22hPassa() {
        Cotacao c = calcular(TipoEvento.REFORMA, terca(20, 0), terca(22, 0), 60);
        assertFalse(c.isBloqueada());
        assertEquals(12, c.getValor());
    }

    // ---------- valores

    @Test
    void festaSimplesDeUmaHora() {
        Cotacao c = calcular(TipoEvento.FESTA, terca(14, 0), terca(15, 0), 60);
        assertEquals(8, c.getValor());
        assertEquals(1, c.getDetalhes().size());
    }

    @Test
    void volumeNoLimiteDoDiaNaoCobraExtra() {
        assertEquals(8, calcular(TipoEvento.FESTA, terca(14, 0), terca(15, 0), 65).getValor());
    }

    @Test
    void volumeUmAcimaDoLimiteCobraUmaFaixa() {
        assertEquals(10, calcular(TipoEvento.FESTA, terca(14, 0), terca(15, 0), 66).getValor());
        assertEquals(10, calcular(TipoEvento.FESTA, terca(14, 0), terca(15, 0), 70).getValor());
    }

    @Test
    void volume71CobraDuasFaixas() {
        assertEquals(12, calcular(TipoEvento.FESTA, terca(14, 0), terca(15, 0), 71).getValor());
    }

    @Test
    void eventoQueEntraNoSilencioPagaHoraDobradaEUsaLimiteDaNoite() {
        assertEquals(24, calcular(TipoEvento.FESTA, terca(21, 0), terca(23, 0), 50).getValor());
        assertEquals(26, calcular(TipoEvento.FESTA, terca(21, 0), terca(23, 0), 51).getValor());
    }

    @Test
    void eventoQueTerminaAs22hNaoPagaSilencio() {
        assertEquals(16, calcular(TipoEvento.FESTA, terca(20, 0), terca(22, 0), 50).getValor());
    }

    @Test
    void feriadoCobraMaisCinquentaPorCento() {
        LocalDateTime natal = LocalDateTime.of(2030, 12, 25, 14, 0);
        assertEquals(12, calcular(TipoEvento.FESTA, natal, natal.plusHours(1), 60).getValor());
    }

    @Test
    void sabadoCobraMaisVintePorCento() {
        LocalDateTime sabado = LocalDateTime.of(2030, 1, 5, 14, 0);
        assertEquals(19, calcular(TipoEvento.FESTA, sabado, sabado.plusHours(2), 60).getValor());
    }

    @Test
    void domingoTambemCobraMaisVintePorCento() {
        LocalDateTime domingo = LocalDateTime.of(2030, 1, 6, 14, 0);
        assertEquals(19, calcular(TipoEvento.FESTA, domingo, domingo.plusHours(2), 60).getValor());
    }

    @Test
    void pedidosDeSilencioDosVizinhosEncarecem() {
        LocalDateTime inicio = terca(14, 0);
        LocalDateTime fim = terca(16, 0);
        pedidos.add(new PedidoSilencio(a301, MotivoSilencio.BEBE, inicio, fim, "vizinho de cima"));
        pedidos.add(new PedidoSilencio(a302, MotivoSilencio.PROVA, inicio, fim, "dois de distancia"));
        pedidos.add(new PedidoSilencio(a402, MotivoSilencio.TRABALHO, inicio, fim, "longe demais"));
        pedidos.add(new PedidoSilencio(a201, MotivoSilencio.DESCANSO, inicio, fim, "o proprio organizador"));
        pedidos.add(new PedidoSilencio(a301, MotivoSilencio.DOENTE, terca(18, 0), terca(19, 0), "outro horario"));

        // 16 + 8 (85 dB) + 8 pontos x 3
        Cotacao c = calcular(TipoEvento.FESTA, inicio, fim, 85);
        assertEquals(48, c.getValor());
    }

    @Test
    void comVinteETresHorasDeAntecedenciaCobraUrgencia() {
        LocalDateTime inicio = AGORA.plusHours(23);
        Cotacao c = calcular(TipoEvento.FESTA, inicio, inicio.plusHours(1), 60);
        assertEquals(12, c.getValor());
    }

    @Test
    void comVinteEQuatroHorasDeAntecedenciaNaoCobraUrgencia() {
        LocalDateTime inicio = AGORA.plusHours(24);
        Cotacao c = calcular(TipoEvento.FESTA, inicio, inicio.plusHours(1), 60);
        assertEquals(8, c.getValor());
        assertNull(c.getErro());
    }
}
