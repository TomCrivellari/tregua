package br.com.tregua.controller;

import br.com.tregua.model.NegocioException;
import br.com.tregua.model.Reclamacao;
import br.com.tregua.repository.BancoDeDados;
import br.com.tregua.service.ReclamacaoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class ReclamacaoController {

    private ReclamacaoService reclamacaoService;
    private BancoDeDados banco;

    public ReclamacaoController(ReclamacaoService reclamacaoService, BancoDeDados banco) {
        this.reclamacaoService = reclamacaoService;
        this.banco = banco;
    }

    @GetMapping("/reclamacoes")
    public String listar(Model model) {
        model.addAttribute("reclamacoes", reclamacaoService.listar());
        model.addAttribute("moradores", banco.getMoradores());
        model.addAttribute("unidades", banco.getCondominio().getUnidades());
        return "reclamacoes";
    }

    @PostMapping("/reclamacoes")
    public String reclamar(@RequestParam Long autor,
                           @RequestParam Long alvo,
                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dataHora,
                           @RequestParam(required = false) Integer decibeis,
                           @RequestParam(required = false) String descricao,
                           RedirectAttributes redirect) {
        try {
            Reclamacao r = reclamacaoService.reclamar(autor, alvo, dataHora, descricao, decibeis);
            redirect.addFlashAttribute("sucesso", "Reclamação " + r.getStatus() + ": " + r.getParecer());
        } catch (NegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/reclamacoes";
    }
}
