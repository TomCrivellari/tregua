package br.com.tregua.funcional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// testa a tela "Agendar evento" pelo navegador (Edge)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AgendarEventoFuncionalTest {

    private static WebDriver driver;

    @LocalServerPort
    private int porta;

    @BeforeAll
    static void abrirNavegador() {
        EdgeOptions opcoes = new EdgeOptions();
        opcoes.addArguments("--headless=new", "--window-size=1280,1000");
        driver = new EdgeDriver(opcoes);
    }

    @AfterAll
    static void fecharNavegador() {
        driver.quit();
    }

    private void preencher(String unidade, String tipo, String descricao, String inicio, String fim, String decibeis) {
        driver.get("http://localhost:" + porta + "/eventos");
        new Select(driver.findElement(By.id("unidade"))).selectByVisibleText(unidade);
        new Select(driver.findElement(By.id("tipo"))).selectByVisibleText(tipo);
        driver.findElement(By.id("descricao")).sendKeys(descricao);
        // campo datetime-local muda conforme o idioma do navegador, entao preenche direto
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = arguments[1]", driver.findElement(By.id("inicio")), inicio);
        js.executeScript("arguments[0].value = arguments[1]", driver.findElement(By.id("fim")), fim);
        WebElement db = driver.findElement(By.id("decibeis"));
        db.clear();
        db.sendKeys(decibeis);
    }

    private void enviar(String nomeDoPrint) throws IOException {
        print(nomeDoPrint + "-antes");
        driver.findElement(By.id("agendar")).click();
        print(nomeDoPrint + "-depois");
    }

    private void print(String nome) throws IOException {
        File arquivo = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path destino = Path.of("target", "telas", nome + ".png");
        Files.createDirectories(destino.getParent());
        Files.copy(arquivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
    }

    private String mensagem(String id) {
        return driver.findElement(By.id(id)).getText();
    }

    @Test
    void caso1_agendaEventoValido() throws IOException {
        preencher("A-302 - Caio Ferraz", "Ensaio musical", "Ensaio da banda",
                "2030-01-09T18:00", "2030-01-09T20:00", "75");
        enviar("caso1-sucesso");

        assertEquals("Evento agendado! Custo: 14 créditos", mensagem("sucesso"));
        assertTrue(driver.findElement(By.id("tabela-eventos")).getText().contains("Ensaio da banda"));
    }

    @Test
    void caso2_fimAntesDoInicio() throws IOException {
        preencher("A-101 - Marina Duarte", "Festa", "Festa errada",
                "2030-01-09T20:00", "2030-01-09T19:00", "70");
        enviar("caso2-fim-antes-do-inicio");

        assertEquals("O fim tem que ser depois do início", mensagem("erro"));
    }

    @Test
    void caso3_reformaNoDomingo() throws IOException {
        preencher("B-101 - Ícaro Menezes", "Reforma", "Quebrar parede",
                "2030-01-06T09:00", "2030-01-06T11:00", "90");
        enviar("caso3-reforma-domingo");

        assertEquals("Reforma não pode em domingo, feriado ou horário de silêncio", mensagem("erro"));
    }

    @Test
    void caso4_volumeAcimaDoMaximo() throws IOException {
        preencher("A-102 - Rafael Okamoto", "Festa", "Show de rock",
                "2030-01-09T15:00", "2030-01-09T17:00", "106");
        enviar("caso4-volume-alto");

        assertEquals("O volume tem que ficar entre 40 e 105 dB", mensagem("erro"));
    }

    @Test
    void caso5_saldoInsuficiente() throws IOException {
        // a Lívia (A-201) ja gastou quase tudo na festa dos dados iniciais
        preencher("A-201 - Lívia Campos", "Festa", "Outra festa",
                "2030-01-09T18:00", "2030-01-09T22:00", "80");
        enviar("caso5-saldo-insuficiente");

        assertTrue(mensagem("erro").startsWith("Saldo insuficiente"));
    }

    @Test
    void caso6_eventoNoPassado() throws IOException {
        preencher("A-401 - Tereza Vasconcelos", "Mudança", "Mudança atrasada",
                "2020-03-10T09:00", "2020-03-10T12:00", "70");
        enviar("caso6-passado");

        assertEquals("Não dá para agendar evento no passado", mensagem("erro"));
    }
}
