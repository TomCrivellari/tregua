package br.com.tregua.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;

// feriados nacionais de data fixa
@Component
public class Feriados {

    private List<MonthDay> datas = List.of(
            MonthDay.of(1, 1),
            MonthDay.of(4, 21),
            MonthDay.of(5, 1),
            MonthDay.of(9, 7),
            MonthDay.of(10, 12),
            MonthDay.of(11, 2),
            MonthDay.of(11, 15),
            MonthDay.of(11, 20),
            MonthDay.of(12, 25)
    );

    public boolean isFeriado(LocalDate data) {
        return datas.contains(MonthDay.from(data));
    }
}
