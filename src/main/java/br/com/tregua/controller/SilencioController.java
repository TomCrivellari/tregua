package br.com.tregua.controller;

import br.com.tregua.model.MotivoSilencio;
import br.com.tregua.model.NegocioException;
import br.com.tregua.repository.BancoDeDados;
import br.com.tregua.service.SilencioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class SilencioController {

    private SilencioService silencioService;
    private BancoDeDados banco;

    public SilencioController(SilencioService silencioService, BancoDeDados banco) {
        this.silencioService = silencioService;
        this.banco = banco;
    }

    @GetMapping("/silencio")
    public String listar(Model model) {
        model.addAttribute("pedidos", silencioService.listar());
        model.addAttribute("unidades", banco.getCondominio().getUnidades());
        model.addAttribute("motivos", MotivoSilencio.values());
        return "silencio";
    }

    @PostMapping("/silencio")
    public String pedir(@RequestParam Long unidade,
                        @RequestParam MotivoSilencio motivo,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime inicio,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fim,
                        @RequestParam(required = false) String observacao,
                        RedirectAttributes redirect) {
        try {
            silencioService.pedir(unidade, motivo, inicio, fim, observacao);
            redirect.addFlashAttribute("sucesso", "Pedido de silêncio registrado");
        } catch (NegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/silencio";
    }
}
