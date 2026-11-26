package br.com.tregua.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

// regras de barulho do condominio (definidas em assembleia)
public class Regras {

    private LocalTime inicioSilencio = LocalTime.of(22, 0);
    private LocalTime fimSilencio = LocalTime.of(8, 0);
    private int limiteDia = 65;
    private int limiteNoite = 50;
    private int limiteMaximo = 105;
    private int duracaoMaxima = 480; // minutos
    private int creditosPorMes = 120;
    private int percentualFundo = 20;

    public boolean horarioDeSilencio(LocalTime hora) {
        // 22h as 8h passa da meia-noite
        return !hora.isBefore(inicioSilencio) || hora.isBefore(fimSilencio);
    }

    public int limiteEm(LocalDateTime dataHora) {
        if (horarioDeSilencio(dataHora.toLocalTime())) {
            return limiteNoite;
        }
        return limiteDia;
    }

    public long minutosEmSilencio(LocalDateTime inicio, LocalDateTime fim) {
        long minutos = 0;
        LocalDateTime t = inicio;
        while (t.isBefore(fim)) {
            if (horarioDeSilencio(t.toLocalTime())) {
                minutos++;
            }
            t = t.plusMinutes(1);
        }
        return minutos;
    }

    public LocalTime getInicioSilencio() {
        return inicioSilencio;
    }

    public LocalTime getFimSilencio() {
        return fimSilencio;
    }

    public int getLimiteDia() {
        return limiteDia;
    }

    public int getLimiteNoite() {
        return limiteNoite;
    }

    public int getLimiteMaximo() {
        return limiteMaximo;
    }

    public int getDuracaoMaxima() {
        return duracaoMaxima;
    }

    public int getCreditosPorMes() {
        return creditosPorMes;
    }

    public int getPercentualFundo() {
        return percentualFundo;
    }
}
