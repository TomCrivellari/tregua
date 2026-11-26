package br.com.tregua.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RelogioSistema implements Relogio {

    @Override
    public LocalDateTime agora() {
        return LocalDateTime.now();
    }
}
