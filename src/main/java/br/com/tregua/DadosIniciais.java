package br.com.tregua;

import br.com.tregua.model.Bloco;
import br.com.tregua.model.Condominio;
import br.com.tregua.model.Medicao;
import br.com.tregua.model.Morador;
import br.com.tregua.model.MotivoSilencio;
import br.com.tregua.model.Regras;
import br.com.tregua.model.TipoEvento;
import br.com.tregua.model.Unidade;
import br.com.tregua.repository.BancoDeDados;
import br.com.tregua.service.EventoService;
import br.com.tregua.service.Feriados;
import br.com.tregua.service.ReclamacaoService;
import br.com.tregua.service.SilencioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

// dados de exemplo para o sistema ja abrir com alguma coisa
@Component
public class DadosIniciais implements CommandLineRunner {

    private BancoDeDados banco;
    private EventoService eventoService;
    private SilencioService silencioService;
    private ReclamacaoService reclamacaoService;
    private Feriados feriados;

    public DadosIniciais(BancoDeDados banco, EventoService eventoService, SilencioService silencioService,
                         ReclamacaoService reclamacaoService, Feriados feriados) {
        this.banco = banco;
        this.eventoService = eventoService;
        this.silencioService = silencioService;
        this.reclamacaoService = reclamacaoService;
        this.feriados = feriados;
    }

    @Override
    public void run(String... args) {
        Condominio condominio = new Condominio("Residencial Aurora", new Regras());
        condominio.addBloco(criarBloco("A", 4, 2));
        condominio.addBloco(criarBloco("B", 3, 2));
        banco.setCondominio(condominio);

        String[] nomes = {
                "Marina Duarte", "Rafael Okamoto", "Lívia Campos", "Otávio Nunes",
                "Joana Alencar", "Caio Ferraz", "Tereza Vasconcelos", "Gustavo Lins",
                "Ícaro Menezes", "Clara Bittencourt", "Vera Antunes", "Henrique Sato", "Paula Gouveia"
        };
        int i = 0;
        for (Unidade unidade : condominio.getUnidades()) {
            unidade.setId(banco.novoId());
            if (i < nomes.length) { // a ultima unidade fica vaga
                String email = nomes[i].split(" ")[0].toLowerCase() + "@email.com";
                Morador morador = new Morador(nomes[i], email, unidade);
                morador.setId(banco.novoId());
                banco.getMoradores().add(morador);
                unidade.getCarteira().creditar(condominio.getRegras().getCreditosPorMes(), "Créditos do mês");
            }
            i++;
        }

        LocalDate hoje = LocalDate.now();
        LocalDate sabado = hoje.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        if (sabado.isBefore(hoje.plusDays(3))) {
            sabado = sabado.plusWeeks(1);
        }
        LocalDate diaUtil = hoje.plusDays(2);
        while (diaUtil.getDayOfWeek() == DayOfWeek.SATURDAY || diaUtil.getDayOfWeek() == DayOfWeek.SUNDAY
                || feriados.isFeriado(diaUtil)) {
            diaUtil = diaUtil.plusDays(1);
        }

        silencioService.pedir(unidade("A-301"), MotivoSilencio.BEBE, sabado.atTime(19, 0), sabado.atTime(23, 59), "Bebê dorme cedo");
        silencioService.pedir(unidade("A-102"), MotivoSilencio.TRABALHO, hoje.plusDays(1).atTime(14, 0),
                hoje.plusDays(1).atTime(16, 0), "Entrevista online");

        eventoService.agendar(unidade("A-201"), TipoEvento.FESTA, "Aniversário da Lívia", sabado.atTime(20, 0), sabado.atTime(23, 30), 85);
        eventoService.agendar(unidade("B-101"), TipoEvento.REFORMA, "Troca do piso", diaUtil.atTime(9, 0), diaUtil.atTime(12, 0), 90);

        LocalDate ontem = hoje.minusDays(1);
        banco.getMedicoes().add(new Medicao(banco.buscarUnidade(unidade("A-402")), 74, ontem.atTime(23, 5)));
        reclamacaoService.reclamar(morador("A-302"), unidade("A-402"), ontem.atTime(23, 10), "Som alto depois das 23h", 72);
        reclamacaoService.reclamar(morador("B-201"), unidade("B-202"), ontem.atTime(15, 0), "Música de tarde", 58);
    }

    private Bloco criarBloco(String nome, int andares, int porAndar) {
        Bloco bloco = new Bloco(nome);
        for (int andar = 1; andar <= andares; andar++) {
            for (int pos = 1; pos <= porAndar; pos++) {
                bloco.criarUnidade(andar, pos);
            }
        }
        return bloco;
    }

    private Long unidade(String nome) {
        for (Unidade u : banco.getCondominio().getUnidades()) {
            if (u.getNome().equals(nome)) {
                return u.getId();
            }
        }
        return null;
    }

    private Long morador(String nomeUnidade) {
        return banco.buscarUnidade(unidade(nomeUnidade)).getMorador().getId();
    }
}
