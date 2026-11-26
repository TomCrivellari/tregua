# Complexidade Ciclomática de McCabe — Projeto Trégua

Este documento apresenta o levantamento do número total de decisões (**N**) no software e o cálculo da **Complexidade Ciclomática (CC)** utilizando a fórmula:

```text
CC = N + 1
```

Onde:
- **CC** = Valor da Complexidade Ciclomática
- **N** = Número de decisões do software (`if`, `else if`, `for`, `while`, operador ternário `? :`)
- **1** = Constante da fórmula de McCabe

---

## 1. Levantamento de Decisões (N) por Classe e Método

### Pacote `service`
- **`CalculadoraCusto`**
  - `calcular(...)`: 12 decisões (8 `if`, 2 `else if`, 1 `for`, 1 ternário `? :`)
  - `validar(...)`: 6 decisões (6 `if`)
- **`AvaliadorReclamacao`**
  - `julgar(...)`: 11 decisões (8 `if`, 1 `else if`, 2 `for`)
- **`EventoService`**
  - `agendar(...)`: 1 decisão (1 `if`)
  - `repassarParaVizinhos(...)`: 5 decisões (3 `if`, 2 `for`)
  - `cancelar(...)`: 1 decisão (1 `if`)
- **`ReclamacaoService`**
  - `reclamar(...)`: 4 decisões (4 `if`)
  - `contarReincidencias(...)`: 2 decisões (1 `for`, 1 `if`)
- **`SilencioService`**
  - `pedir(...)`: 3 decisões (3 `if`)
- **Subtotal `service`**: **45 decisões**

### Pacote `model`
- **`Carteira`**
  - `creditar(...)`: 1 decisão (1 `if`)
  - `debitar(...)`: 2 decisões (2 `if`)
- **`Condominio`**
  - `getUnidades()`: 1 decisão (1 `for`)
- **`Regras`**
  - `limiteEm(...)`: 1 decisão (1 `if`)
  - `minutosEmSilencio(...)`: 2 decisões (1 `while`, 1 `if`)
- **`Unidade`**
  - `distancia(...)`: 1 decisão (1 `if`)
  - `afetadaPor(...)`: 2 decisões (1 `if`, 1 `else if`)
- **Subtotal `model`**: **10 decisões**

### Pacote `repository`
- **`BancoDeDados`**
  - `buscarUnidade(...)`: 2 decisões (1 `for`, 1 `if`)
  - `buscarMorador(...)`: 2 decisões (1 `for`, 1 `if`)
  - `buscarEvento(...)`: 2 decisões (1 `for`, 1 `if`)
- **Subtotal `repository`**: **6 decisões**

### Pacote Raiz (`br.com.tregua`)
- **`DadosIniciais`**
  - `run(...)`: 4 decisões (1 `for`, 2 `if`, 1 `while`)
  - `criarBloco(...)`: 2 decisões (2 `for`)
  - `unidade(...)`: 2 decisões (1 `for`, 1 `if`)
- **Subtotal Raiz**: **8 decisões**

---

## 2. Cálculo Final da Complexidade Ciclomática (`CC = N + 1`)

Somando todas as decisões (**N**) encontradas no software:

```text
N = 45 (service) + 10 (model) + 6 (repository) + 8 (DadosIniciais)
N = 69 decisões
```

Aplicando a fórmula **`CC = N + 1`**:

```text
CC = N + 1
CC = 69 + 1
CC = 70
```

- **Número total de decisões (`N`)**: **69**
- **Complexidade Ciclomática (`CC`)**: **70**

---

## 3. Complexidade Ciclomática por Método (do Maior para o Menor)

Abaixo está o ranking de todos os métodos do sistema aplicando a fórmula **`CC = N + 1`** individualmente em cada método, ordenados da **maior** para a **menor** Complexidade Ciclomática:

| Posição | Classe | Método | Decisões (`N`) | Cálculo (`CC = N + 1`) | Complexidade Ciclomática (`CC`) |
| :---: | :--- | :--- | :---: | :---: | :---: |
| **1º** | `CalculadoraCusto` | `calcular(...)` | 12 | `12 + 1` | **13** |
| **2º** | `AvaliadorReclamacao` | `julgar(...)` | 11 | `11 + 1` | **12** |
| **3º** | `CalculadoraCusto` | `validar(...)` | 6 | `6 + 1` | **7** |
| **4º** | `EventoService` | `repassarParaVizinhos(...)` | 5 | `5 + 1` | **6** |
| **5º** | `ReclamacaoService` | `reclamar(...)` | 4 | `4 + 1` | **5** |
| **6º** | `DadosIniciais` | `run(...)` | 4 | `4 + 1` | **5** |
| **7º** | `SilencioService` | `pedir(...)` | 3 | `3 + 1` | **4** |
| **8º** | `ReclamacaoService` | `contarReincidencias(...)` | 2 | `2 + 1` | **3** |
| **9º** | `Carteira` | `debitar(...)` | 2 | `2 + 1` | **3** |
| **10º** | `Regras` | `minutosEmSilencio(...)` | 2 | `2 + 1` | **3** |
| **11º** | `Unidade` | `afetadaPor(...)` | 2 | `2 + 1` | **3** |
| **12º** | `BancoDeDados` | `buscarUnidade(...)` | 2 | `2 + 1` | **3** |
| **13º** | `BancoDeDados` | `buscarMorador(...)` | 2 | `2 + 1` | **3** |
| **14º** | `BancoDeDados` | `buscarEvento(...)` | 2 | `2 + 1` | **3** |
| **15º** | `DadosIniciais` | `criarBloco(...)` | 2 | `2 + 1` | **3** |
| **16º** | `DadosIniciais` | `unidade(...)` | 2 | `2 + 1` | **3** |
| **17º** | `EventoService` | `agendar(...)` | 1 | `1 + 1` | **2** |
| **18º** | `EventoService` | `cancelar(...)` | 1 | `1 + 1` | **2** |
| **19º** | `Carteira` | `creditar(...)` | 1 | `1 + 1` | **2** |
| **20º** | `Condominio` | `getUnidades()` | 1 | `1 + 1` | **2** |
| **21º** | `Regras` | `limiteEm(...)` | 1 | `1 + 1` | **2** |
| **22º** | `Unidade` | `distancia(...)` | 1 | `1 + 1` | **2** |
| **23º** | *Demais classes* | *Métodos lineares (getters, setters, construtores, etc.)* | 0 | `0 + 1` | **1** |

---

## 4. Classes e Métodos que Devem Ser Mais Testados (Análise das Métricas)

Com base na **Complexidade Ciclomática de McCabe (`CC`)** levantada no código-fonte e no cruzamento com as **Métricas de Design Orientado a Objetos** (`Metricas.xlsx` — Chidamber & Kemerer e Lorenz & Kidd), a priorização de testes deve focar nos seguintes alvos:

### 4.1. Prioridade Máxima — Alta Complexidade Lógica (Caixa-Branca / Testes Unitários)
A Complexidade Ciclomática indica a quantidade de caminhos linearmente independentes (número mínimo de casos de teste para cobrir todos os desvios lógicos). Os métodos com maior `CC` concentram o maior risco de defeitos em regras de negócio:

1. **`CalculadoraCusto` — métodos `calcular(...)` (`CC = 13`) e `validar(...)` (`CC = 7`)**
   - **Por que testar intensivamente**: Somados, possuem **18 decisões** (mais de 26% de todas as decisões do software). O método `calcular` combina múltiplas regras cumulativas (custo base por tipo, dobra em horário de silêncio, faixas de 5 dB acima do limite, feriado vs. fim de semana, pontuação de pedidos de silêncio de vizinhos diretos ou afetados e taxa de antecedência < 24h), enquanto `validar` concentra 6 condições de bloqueio (incluindo restrições específicas para `REFORMA`).
   - **Foco dos testes**: Particionamento em classes de equivalência, análise de valor limite (40 dB, 50 dB, 65 dB, 105 dB, 24h, 480 min) e uso de *mocks* para `Relogio` e `Feriados`.

2. **`AvaliadorReclamacao` — método `julgar(...)` (`CC = 12`)**
   - **Por que testar intensivamente**: É o segundo método mais complexo do sistema (`11 decisões` em um único método, além do maior `APPM = 5` na planilha de métricas). Decide o parecer da reclamação (`IMPROCEDENTE`, `INCONCLUSIVA` ou `PROCEDENTE`) cruzando autoria, janela temporal de ±20 minutos das medições, tolerância de +3 dB em eventos agendados, escalonamento de multas (10, 20 ou 35 créditos) e dobra por reincidência (≥ 2).
   - **Foco dos testes**: Cobertura de todos os ramos de decisão e limites de fronteira (ex.: medição exatamente em 20 min vs. 21 min; excesso de 5 dB, 6 dB, 15 dB e 16 dB; 1 vs. 2 reincidências).

### 4.2. Prioridade Alta — Orquestração e Regras de Negócio com Acoplamento (Testes de Integração e Unitários)

3. **`EventoService` — especialmente `repassarParaVizinhos(...)` (`CC = 6`), `agendar(...)` (`CC = 2`) e `cancelar(...)` (`CC = 2`)**
   - **Por que testar intensivamente**: Além de possuir a 4ª maior `CC` por método (`CC = 6`), aparece no **Top 3 de prioridade ALTA** em `Metricas.xlsx` (`WMC = 4`, `RFC = 14`, `CBO = 3`). Gerencia o débito na carteira do organizador, o cálculo da retenção de 20% do fundo do condomínio, a divisão inteira entre vizinhos ocupados afetados pelo ruído e o destino das sobras de arredondamento.

4. **`ReclamacaoService` — métodos `reclamar(...)` (`CC = 5`) e `contarReincidencias(...)` (`CC = 3`)**
   - **Por que testar intensivamente**: Possui **6 decisões** na classe e alto índice de resposta (`RFC = 10`, `CBO = 3`). Controla a janela móvel de 30 dias para reincidências e a cobrança de multa limitada ao saldo disponível na carteira do infrator (`Math.min(multa, saldo)`).

5. **`SilencioService` — método `pedir(...)` (`CC = 4`)**
   - **Por que testar intensivamente**: Valida restrições temporais críticas dos pedidos de silêncio (fim após início, proibição de horário passado e limite máximo de 12 horas).

### 4.3. Prioridade Estrutural — Classes Centrais de Domínio e Estado (Destaque no Top 20% de `Metricas.xlsx`)

6. **`Unidade` — métodos `afetadaPor(...)` (`CC = 3`) e `distancia(...)` (`CC = 2`)**
   - **Por que testar intensivamente**: É a **2ª classe mais crítica no ranking de design** (`CBO = 7`, `RFC = 12`, `WMC = 4`). Um erro no cálculo de distância entre andares/blocos ou no raio de alcance acústico (80 dB → distância 2; 95 dB → distância 3) impacta simultaneamente o preço dos eventos em `CalculadoraCusto` e o repasse financeiro em `EventoService`.

7. **`Regras` — métodos `minutosEmSilencio(...)` (`CC = 3`) e `limiteEm(...)` (`CC = 2`)**
   - **Por que testar intensivamente**: Está no **Top 5 de prioridade ALTA no design** (`NIV = 8`, `WMC = 3`). O laço minuto a minuto de `minutosEmSilencio` lida com a virada de meia-noite (`22:00` às `08:00`) e é dependência direta da precificação e validação de eventos.

8. **`Carteira` — métodos `debitar(...)` (`CC = 3`) e `creditar(...)` (`CC = 2`)**
   - **Por que testar intensivamente**: Protege a integridade de saldo de todas as unidades contra valores negativos/zero e saldo insuficiente.

9. **`BancoDeDados` — métodos `buscarUnidade(...)` (`CC = 3`), `buscarMorador(...)` (`CC = 3`) e `buscarEvento(...)` (`CC = 3`)**
   - **Por que testar intensivamente**: É a **1ª colocada no ranking de design** (`CBO = 13`, `RFC = 29`) por ser acoplada a quase todas as classes do sistema, exigindo testes para buscas existentes e lançamento de `NegocioException` quando o ID não existe.

---

### 4.4. Síntese Comparativa: Métricas de Design vs. Complexidade Ciclomática

| Classe | Destaque no Design (`Metricas.xlsx`) | Destaque no Código (`CC` de McCabe) | Conclusão para Estratégia de Testes |
| :--- | :--- | :--- | :--- |
| **`CalculadoraCusto`** | 9º lugar (`WMC = 3`, `CBO = 2`) | **1º lugar em decisões (`N = 18`, `CC` até `13`)** | Crítica para **testes unitários exaustivos e mocks** (alta complexidade interna oculta no diagrama de classes). |
| **`AvaliadorReclamacao`** | 17º lugar (`WMC = 1`, `APPM = 5`) | **2º lugar em decisões (`N = 11`, `CC = 12`)** | Crítica para **testes unitários de ramos e valores-limite** (toda a lógica está concentrada em `julgar`). |
| **`EventoService`** | **3º lugar — ALTA** (`WMC = 4`, `RFC = 14`) | **3º lugar no negócio (`N = 7`, `CC` até `6`)** | Crítica tanto em **testes unitários quanto de integração/funcionais**. |
| **`Unidade` e `Regras`** | **2º e 5º lugares — ALTA** (`CBO = 7`, `NIV = 8`) | **`CC = 3`** em seus métodos principais | Críticas por **alto acoplamento**: defeitos nelas propagam erros para todo o sistema. |
| **`BancoDeDados`** | **1º lugar — ALTA** (`CBO = 13`, `RFC = 29`) | **`CC = 3`** nos 3 métodos de busca | Crítica em **testes de integração** pelo alto acoplamento aferente e eferente. |


