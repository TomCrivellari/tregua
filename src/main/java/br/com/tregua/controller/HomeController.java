package br.com.tregua.controller;

import br.com.tregua.repository.BancoDeDados;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private BancoDeDados banco;

    public HomeController(BancoDeDados banco) {
        this.banco = banco;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("condominio", banco.getCondominio());
        model.addAttribute("unidades", banco.getCondominio().getUnidades());
        return "index";
    }
}
