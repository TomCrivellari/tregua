package br.com.tregua.controller;

import br.com.tregua.model.Evento;
import br.com.tregua.model.NegocioException;
import br.com.tregua.model.TipoEvento;
import br.com.tregua.repository.BancoDeDados;
import br.com.tregua.service.EventoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class EventoController {

    private EventoService eventoService;
    private BancoDeDados banco;

    public EventoController(EventoService eventoService, BancoDeDados banco) {
        this.eventoService = eventoService;
        this.banco = banco;
    }

    @GetMapping("/eventos")
    public String listar(Model model) {
        model.addAttribute("eventos", eventoService.listar());
        model.addAttribute("unidades", banco.getCondominio().getUnidades());
        model.addAttribute("tipos", TipoEvento.values());
        return "eventos";
    }

    @PostMapping("/eventos")
    public String agendar(@RequestParam Long unidade,
                          @RequestParam TipoEvento tipo,
                          @RequestParam String descricao,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime inicio,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fim,
                          @RequestParam int decibeis,
                          RedirectAttributes redirect) {
        try {
            Evento evento = eventoService.agendar(unidade, tipo, descricao, inicio, fim, decibeis);
            redirect.addFlashAttribute("sucesso", "Evento agendado! Custo: " + evento.getCusto() + " créditos");
        } catch (NegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/eventos";
    }

    @PostMapping("/eventos/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            eventoService.cancelar(id);
            redirect.addFlashAttribute("sucesso", "Evento cancelado");
        } catch (NegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/eventos";
    }
}
