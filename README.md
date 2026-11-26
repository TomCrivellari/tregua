# Trégua — Bolsa de Créditos de Ruído entre Vizinhos

**Trégua** é uma aplicação web desenvolvida em **Java 21** com **Spring Boot** e **Thymeleaf** que implementa um sistema de economia de créditos para gerenciar a convivência acústica em condomínios residenciais.

O projeto também é utilizado como estudo de caso na disciplina de **Teste de Software**, contemplando testes unitários, testes com dublês/mocks, testes funcionais (E2E com Selenium), cobertura de código (JaCoCo) e análise de métricas de software.

---

## Como o Sistema Funciona

Em vez de tratar o barulho apenas com proibições estáticas, o **Trégua** cria uma **bolsa de créditos de ruído**:

1. **Cota Mensal de Créditos**:
   - Cada unidade habitacional possui uma carteira digital que recebe **120 créditos por mês** (configurável nas regras do condomínio).
2. **Agendamento de Eventos Barulhentos**:
   - Quando um morador deseja realizar uma atividade que gera ruído (festa, reforma, ensaio musical ou mudança), ele agenda o evento informando horário de início/fim e o volume estimado em decibéis (dB).
   - O sistema calcula automaticamente o **custo em créditos** e debita da carteira do organizador.
3. **Compensação aos Vizinhos Afetados**:
   - Os créditos pagos pelo organizador não desaparecem: **20%** vão para o fundo do condomínio e os **80%** restantes são divididos igualmente entre as unidades vizinhas alcançadas pelo barulho (calculado pela distância em andares/posição dentro do mesmo bloco e pela intensidade em dB).
4. **Pedidos de Silêncio**:
   - Moradores que precisam de tranquilidade em determinado período (ex.: bebê dormindo, pessoa doente, pós-plantão, prova, home office) podem registrar um **Pedido de Silêncio** (de até 12 horas).
   - Caso um vizinho agende um evento barulhento que se sobreponha ao pedido e afete a unidade solicitante, o evento fica significativamente mais caro.
5. **Reclamações e Julgamento Automático**:
   - Moradores podem registrar reclamações de barulho contra outras unidades.
   - O sistema avalia automaticamente as medições de decibéis registradas num raio de **±20 minutos** do horário reclamado, verifica se havia algum evento pago cobrindo aquele volume (com tolerância de +3 dB) e emite o parecer (*Procedente*, *Improcedente* ou *Inconclusiva*).
   - Se a reclamação for **Procedente**, a unidade infratora sofre uma multa em créditos (dobrada em caso de reincidência nos últimos 30 dias), cujo valor é transferido como indenização para quem reclamou.

---

## Regras de Negócio Principais

### Parâmetros Padrão do Condomínio (`Regras`)
| Parâmetro | Valor Padrão |
| :--- | :--- |
| **Horário de silêncio** | Das `22:00` às `08:00` |
| **Limite diurno** | `65 dB` |
| **Limite noturno (horário de silêncio)** | `50 dB` |
| **Volume permitido em eventos** | Entre `40 dB` e `105 dB` |
| **Duração máxima de um evento** | `8 horas` (`480 minutos`) |
| **Cota mensal por unidade** | `120 créditos` |
| **Retenção do fundo do condomínio** | `20%` do custo do evento |

### Cálculo de Custo de Eventos (`CalculadoraCusto`)
- **Custo Base por Hora** (arredondado para cima):
  - `FESTA`: 8 créditos/h
  - `REFORMA`: 6 créditos/h *(proibida aos domingos, feriados ou em horário de silêncio)*
  - `ENSAIO` (Ensaio musical): 5 créditos/h
  - `MUDANCA`: 4 créditos/h
- **Adicional de Horário de Silêncio**: horas dentro da faixa `22:00–08:00` custam o dobro (`+1x valorHora` por hora em silêncio).
- **Excesso de Decibéis**: `+2 créditos` para cada faixa de `5 dB` acima do limite vigente (diurno ou noturno).
- **Feriado ou Fim de Semana**:
  - Feriado nacional: `+50%` sobre o subtotal acumulado.
  - Sábado ou domingo (não feriado): `+20%` sobre o subtotal acumulado.
- **Sobreposição com Pedidos de Silêncio**:
  - Cada motivo possui um peso: *Bebê dormindo* (`3`), *Pessoa doente* (`3`), *Descanso pós-plantão* (`2`), *Véspera de prova* (`2`), *Reunião / home office* (`1`), *Descanso* (`1`).
  - Vizinho direto (distância 1): `peso * 2` pontos.
  - Vizinho afetado pelo alcance do volume (`>= 80 dB` alcança distância 2; `>= 95 dB` alcança distância 3): `peso` pontos.
  - Custo adicional: `totalDePontos * 3` créditos.
- **Aviso em Cima da Hora**: agendamentos feitos com menos de **24 horas de antecedência** sofrem acréscimo de `+50%` no custo total.

### Julgamento de Reclamações (`AvaliadorReclamacao`)
- **Improcedente**:
  - Reclamação contra a própria unidade;
  - Pico de ruído coberto por evento ativo da unidade alvo (até `volumeDoEvento + 3 dB`);
  - Pico de ruído dentro do limite permitido para o horário.
- **Inconclusiva**:
  - Nenhuma medição encontrada para a unidade alvo em até 20 minutos antes ou depois do horário informado.
- **Procedente** (com aplicação de multa):
  - Excesso de até `5 dB`: **10 créditos** de multa;
  - Excesso de `6 dB` a `15 dB`: **20 créditos** de multa;
  - Excesso acima de `15 dB`: **35 créditos** de multa;
  - **Reincidência**: caso a unidade alvo já possua **2 ou mais** reclamações procedentes nos 30 dias anteriores, a multa é **dobrada**.

---

## Arquitetura e Estrutura do Projeto

```text
tregua/
├── src/
│   ├── main/
│   │   ├── java/br/com/tregua/
│   │   │   ├── controller/       # Controladores MVC (Home, Evento, Silencio, Reclamacao)
│   │   │   ├── model/            # Entidades de domínio, enums e regras de negócio
│   │   │   ├── repository/       # BancoDeDados em memória
│   │   │   ├── service/          # Serviços, CalculadoraCusto, AvaliadorReclamacao, Relogio, Feriados
│   │   │   ├── DadosIniciais.java# Carga inicial de exemplo ("Residencial Aurora")
│   │   │   └── TreguaApplication.java
│   │   └── resources/
│   │       ├── static/           # Folha de estilos (estilo.css)
│   │       ├── templates/        # Páginas Thymeleaf (index, eventos, silencio, reclamacoes, menu)
│   │       └── application.properties
│   └── test/
│       └── java/br/com/tregua/
│           ├── funcional/        # Testes funcionais E2E com Selenium WebDriver
│           └── service/          # Testes unitários e com mocks (CalculadoraCusto, AvaliadorReclamacao)
├── diagrama-classes.mdj          # Diagrama de classes do projeto (StarUML)
├── Metricas.xlsx                 # Planilha de métricas de software e testes
└── pom.xml                       # Configuração Maven, dependências e plugins (JaCoCo, Surefire)
```

---

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 4.0.7** (`spring-boot-starter-webmvc`, `spring-boot-starter-thymeleaf`, `spring-boot-starter-test`)
- **Thymeleaf** (renderização HTML server-side)
- **JUnit 5 & Mockito** (testes unitários e dublês de teste)
- **Selenium Java** (testes funcionais automatizados no navegador)
- **JaCoCo (`0.8.13`) & Maven Surefire Report** (relatórios de execução e cobertura de testes)

---

## Como Executar o Projeto

### Pré-requisitos
- **JDK 21** instalado e configurado no `PATH`
- **Apache Maven 3.9+**

### Subindo a Aplicação Web
Na raiz do projeto, execute:

```bash
mvn spring-boot:run
```

A aplicação será iniciada na porta **`8085`** (definida em `src/main/resources/application.properties`) já populada com os dados de exemplo do condomínio **Residencial Aurora** (Blocos A e B, moradores, eventos agendados, pedidos de silêncio e reclamações).

Acesse no navegador:
- **Painel Principal (Unidades e Carteiras)**: `http://localhost:8085/`
- **Eventos de Ruído**: `http://localhost:8085/eventos`
- **Pedidos de Silêncio**: `http://localhost:8085/silencio`
- **Reclamações**: `http://localhost:8085/reclamacoes`

---

## Executando os Testes e Relatórios de Cobertura

Para rodar a suíte de testes e gerar o relatório de cobertura do **JaCoCo**:

```bash
mvn test
```

Após a execução, o relatório HTML de cobertura estará disponível em:
- `target/site/jacoco/index.html`
