# Atividade 10 — Teste de Integração (FI e FIT)

Aplicação da técnica de ordenação de classes para teste de integração da apostila (Fator de Influência — **FI** e Fator de Influência Tardia — **FIT**) ao projeto Trégua. A planilha completa está em [TesteIntegracao.xlsx](TesteIntegracao.xlsx).

## 1. Técnica

- **FI**: número de classes que dependem diretamente da classe (serão integradas depois dela).
- **FIT**: soma dos FI das classes de que ela depende diretamente (ainda não integradas). Quanto maior o FIT, mais tarde a classe é integrada.
- **Processo**: integrar primeiro as classes com `FIT = 0`; remover as integradas, recalcular o FIT das demais e repetir até integrar todas. Quando só restam classes em ciclo (nenhuma com `FIT = 0`), integra-se a de menor FIT usando **stubs** para seus servidores pendentes.
- **Fonte das relações**: diagrama de classes [diagrama-classes.mdj](diagrama-classes.mdj) (23 classes; enumerações não entram, como em `Metricas.xlsx`). Critérios de precedência: Todo-Parte, Navegabilidade e Dependência. Associações sem navegabilidade definida são tratadas como bidirecionais.

## 2. FI de cada classe

| Classe | FI | Clientes (classes que dependem dela) |
| :--- | :---: | :--- |
| `BancoDeDados` | 7 | EventoService, SilencioService, ReclamacaoService, HomeController, EventoController, SilencioController, ReclamacaoController |
| `Unidade` | 6 | Bloco, Morador, Evento, PedidoSilencio, Medicao, Reclamacao |
| `Morador` | 3 | Unidade, Reclamacao, BancoDeDados |
| `Bloco` | 2 | Condominio, Unidade |
| `Condominio`, `Carteira`, `Transacao`, `Regras`, `Evento`, `PedidoSilencio`, `Medicao`, `Reclamacao`, `Feriados`, `CalculadoraCusto`, `AvaliadorReclamacao`, `EventoService`, `SilencioService`, `ReclamacaoService` | 1 | uma classe cliente cada |
| `Cotacao`, `HomeController`, `EventoController`, `SilencioController`, `ReclamacaoController` | 0 | nenhuma |

## 3. Iterações (FIT = 0 ⇒ integra)

| Iteração | Classes integradas | Observação |
| :---: | :--- | :--- |
| 1 | `AvaliadorReclamacao`, `Feriados`, `Regras`, `Transacao`, `Cotacao` | Folhas, `FIT = 0` |
| 2 | `CalculadoraCusto`, `Carteira` | Servidores (`Feriados`, `Transacao`) já integrados |
| 3 | `Unidade` | Ciclo `Bloco ↔ Unidade ↔ Morador`: nenhuma classe com `FIT = 0`; menor FIT do ciclo é `Unidade` (`FIT = 5`) ⇒ **stubs** |
| 4 | `Morador`, `Bloco`, `Evento`, `Medicao`, `PedidoSilencio` | `Unidade` real disponível; stubs substituídos |
| 5 | `Condominio`, `Reclamacao` | |
| 6 | `BancoDeDados` | |
| 7 | `EventoService`, `ReclamacaoService`, `SilencioService`, `HomeController` | |
| 8 | `EventoController`, `ReclamacaoController`, `SilencioController` | |

## 4. Ordem de construção/integração (23 passos)

1. `AvaliadorReclamacao` → 2. `Feriados` → 3. `Regras` → 4. `Transacao` → 5. `Cotacao` → 6. `CalculadoraCusto` → 7. `Carteira` → 8. **`Unidade`** (com `StubBloco` e `StubMorador`) → 9. `Morador` → 10. `Bloco` → 11. `Evento` → 12. `Medicao` → 13. `PedidoSilencio` → 14. `Condominio` → 15. `Reclamacao` → 16. `BancoDeDados` → 17. `EventoService` → 18. `ReclamacaoService` → 19. `SilencioService` → 20. `HomeController` → 21. `EventoController` → 22. `ReclamacaoController` → 23. `SilencioController`

**Total de stubs: 2 · Tamanho: 23 classes.**

## 5. Stubs necessários

| Stub | Substitui | Criado no passo | Substituído no passo | O que simula |
| :--- | :--- | :---: | :---: | :--- |
| `StubBloco` | `Bloco` | 8 (`Unidade`) | 10 (`Bloco` real) | `getNome()` com valor fixo e instância usada na comparação de `Unidade.distancia()` |
| `StubMorador` | `Morador` | 8 (`Unidade`) | 9 (`Morador` real) | Objeto guardado/devolvido por `Unidade.setMorador()` / `getMorador()` |

O ciclo `Bloco ↔ Unidade ↔ Morador` é o único que exige stubs: `Bloco` (todo) precisa de `Unidade` (parte), `Unidade` guarda referência ao `Bloco`, e `Morador` ↔ `Unidade` se referenciam mutuamente. As camadas `repository`, `service` e `controller` são integradas sem stubs, e cada passo é disparado por um driver (teste JUnit).

> [!NOTE]
> A análise segue o diagrama de classes, como na apostila. Dependências existentes só no código (por exemplo `CalculadoraCusto` usando `Evento`, `Regras` e `PedidoSilencio`, ou `Relogio`) não constam no diagrama; pelo código lido elas não criam novos ciclos.
