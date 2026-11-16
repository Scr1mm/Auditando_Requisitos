# Auditando Requisitos em Java — Sistema Supervisório Industrial

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-projeto-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![VS Code](https://img.shields.io/badge/VS%20Code-editor-007ACC?style=for-the-badge&logo=visualstudiocode&logoColor=white)
![Status](https://img.shields.io/badge/status-concluído-brightgreen?style=for-the-badge)
![Requisitos](https://img.shields.io/badge/requisitos-29-blue?style=for-the-badge)

Projeto desenvolvido em Java para a Unidade Curricular **Lógica de Programação e Algoritmos (LOPAL)**.
Um sistema supervisório de console que monitora uma **célula robotizada de produção**. Os algoritmos
implementam os requisitos auditados no relatório de auditoria (padrão IEEE 830 / ISO 25010).

---

## Sobre o projeto

O sistema simula a tela de supervisão de uma linha industrial com robô, câmera de inspeção e esteira.
Cada funcionalidade foi criada para atender a um requisito **funcional (RF)** ou **não funcional (RNF)**
mapeado na auditoria. Os dados de produção, comunicação, inspeção e telemetria vêm de arquivos **CSV**.

O projeto aplica os seguintes conceitos:

- Variáveis, tipos de dados e casting;
- Entrada de dados com `Scanner`;
- Estruturas de decisão (`if/else` e `switch`);
- Estruturas de repetição (`for`, `while` e `do-while`);
- Vetores e matrizes;
- Funções (métodos `static`);
- Leitura e escrita de arquivos (`BufferedReader` e `BufferedWriter`);
- Manipulação de Strings (`split`, `trim`, `contains`, `equals`);
- Tratamento de exceções (`try/catch`).

---

## Requisitos implementados

| ID | Requisito | Tipo | Onde está no código | Menu |
|:--:|-----------|------|---------------------|:----:|
| RF-01 | Botão de Parada de Emergência | Funcional | `Emergencia.acionarParada()` | 99 |
| RF-02 | Exibição do Status do Robô | Funcional | `Robo.menuStatusRobo()` | 1 |
| RF-03 | Status do Programa | Funcional | `Robo.menuStatusPrograma()` | 2 |
| RF-04 | Status da Comunicação | Funcional | `Robo.exibirStatusComunicacao()` | 3 |
| RF-05 | Contador de Peças Processadas | Funcional | `Producao.exibirContador()` | 9 |
| RF-06 | Medição do Tempo de Ciclo | Regra de Negócio | `Producao.exibirTempoCiclo()` | 10 |
| RF-07 | Indicador de Taxa de Acerto | Integração | `Camera.exibirTaxaAcerto()` | 6 |
| RF-08 | Visualização de Câmera/Vídeo | Integração | `Camera.menuCamera()` | 4 |
| RF-09 | Indicador de Gravação de Vídeo | Integração | `Camera.indicadorGravacao()` | 4 |
| RF-10 | Status de Processamento da Câmera | Integração | `Camera.exibirStatusProcessamento()` | 5 |
| RF-12 | Monitoramento da Eficiência (OEE) | Funcional | `Producao.exibirOEE()` | 11 |
| RF-13 | Métricas de Disponibilidade Mecânica | Funcional | `Producao.exibirDisponibilidade()` | 12 |
| RF-14 | Painel de Monitoramento de Peças | Funcional | `Producao.exibirPainel()` | 8 |
| RF-15 | Navegação por Sidebar | Funcional | `App.exibirSidebar()` | — |
| RF-16 | Identificação e Logotipo da Instituição | Funcional | `Tela.logotipo()` | — |
| RF-17 | Relógio do Sistema | Funcional | `Tela.exibirRelogio()` | 15 |
| RF-18 | Central de Notificações | Funcional | `Notificacoes.menuNotificacoes()` | 14 |
| RF-19 | Perfil do Usuário e Logout | Funcional | `Acesso.menuPerfil()` | 20 |
| RNF-01 | Interface no Modo Escuro | Não funcional | `Tela.alternarTema()` | 20 |
| RNF-03 | Atualização de Telemetria | Não funcional | `SistemaWeb.exibirTelemetria()` | 13 |
| RNF-04 | Destaque Visual para Emergência | Não funcional | `Emergencia.exibirFaixaEmergencia()` | — |
| RNF-05 | Responsividade da GUI | Não funcional | `Tela.ajustarLargura()` | 20 |
| RNF-06 | Latência do Vídeo Streaming | Não funcional | `Camera.exibirLatencia()` | 7 |
| RF-08 | Codificação Visual por Cores | Não funcional | `Tela.verde()` / `amarelo()` / `vermelho()` | — |
| RNF-10 | Controle de Acesso Baseado em Perfis | Não funcional | `Acesso.temPermissao()` | — |
| RNF-11 | Tempo de Resposta da Emergência | Não funcional | `Emergencia.exibirTemposResposta()` | 16 |
| RF-0010 | Disponibilidade do Web App | Não funcional | `SistemaWeb.exibirDisponibilidadeWeb()` | 18 |
| RF-0013 | Persistência de Logs | Não funcional | `Log.registrar()` / `Log.exibirLogs()` | 17 |
| RNF-0014 | Arquitetura Web | Não funcional | `SistemaWeb.exibirArquitetura()` | 19 |

> Os IDs seguem exatamente o relatório de auditoria (por isso existem IDs como `RF-0010` e dois `RF-08`).

### Apontamento da auditoria corrigido

O relatório do **RF-09** registrou: *"O sistema ainda não mostra um sinalizador visual de gravação."*
No sistema, o indicador **`● REC`** em vermelho aparece no cabeçalho de **todas as telas** enquanto a câmera grava.

---

## Funcionalidades

- **Login com 3 tentativas**, lendo os usuários do arquivo `usuarios.csv`;
- **Perfis de acesso**: OPERADOR, SUPERVISOR e ADMIN, cada um com permissões diferentes;
- **Parada de emergência**, com faixa vermelha em todas as telas, medição do tempo de resposta e rearme só pelo supervisor;
- **Status do robô**: estado, velocidade, posição e temperatura dos 6 eixos, além de simulação de falha;
- **Programa do robô**: passo a passo, barra de progresso e contagem de ciclos;
- **Comunicação** com CLP, robô, câmera e servidores, classificada pelo tempo de ping;
- **Câmera ao vivo** desenhada com uma **matriz**, com gravação e contagem de quadros;
- **Inspeção por visão**: taxa de acerto, tempo de processamento e latência do vídeo;
- **Indicadores de produção**: contador de peças, tempo de ciclo, OEE, disponibilidade, MTBF e MTTR;
- **Telemetria**: verificação do intervalo entre as atualizações;
- **Central de notificações** guardada em um **vetor** de 10 posições;
- **Logs gravados em arquivo** (`log_eventos.csv`) com data, usuário e tipo do evento;
- **Modo escuro/claro**, **cores por status** (verde, amarelo e vermelho) e **menu responsivo** (1 ou 2 colunas).

---

## Menu

```
------------------------------------------------------------
  MENU DE NAVEGAÇÃO
------------------------------------------------------------
1  - Status do Robô                   2  - Status do Programa
3  - Status da Comunicação            4  - Câmera ao Vivo / Gravação
5  - Processamento da Câmera          6  - Taxa de Acerto
7  - Latência do Vídeo                8  - Painel de Peças
9  - Contador de Peças                10 - Tempo de Ciclo
11 - Eficiência (OEE)                 12 - Disponibilidade Mecânica
13 - Telemetria                       14 - Central de Notificações
15 - Relógio do Sistema               16 - Tempo de Resp. Emergência
17 - Logs do Sistema       [SUP]      18 - Disponibilidade Web   [ADM]
19 - Arquitetura Web       [ADM]      20 - Perfil / Tema / Logout
99 - PARADA DE EMERGÊNCIA             98 - Rearmar emergência    [SUP]
0  - Sair do sistema
```

### Usuários para teste

| Usuário | Senha | Perfil | Permissões |
|---------|-------|--------|------------|
| `operador` | `1234` | OPERADOR | Monitoramento e parada de emergência |
| `supervisor` | `1234` | SUPERVISOR | + rearme da emergência e logs |
| `admin` | `admin` | ADMIN | + disponibilidade web e arquitetura |

---

## Arquivos de dados (CSV)

| Arquivo | Colunas | Usado em |
|---------|---------|----------|
| `usuarios.csv` | usuario, senha, perfil, nome | Login e perfis |
| `comunicacao.csv` | dispositivo, ip, ultimo_ping_ms | RF-04 |
| `producao.csv` | hora, pecas_boas, pecas_rejeitadas, tempo_ciclo_s, minutos_parado | RF-05, 06, 12, 13, 14 |
| `inspecoes.csv` | peca, resultado, tempo_ms | RF-07, RF-10 |
| `latencia_video.csv` | amostra, latencia_ms | RNF-06 |
| `telemetria.csv` | tempo_ms, temperatura_c, vibracao_mm_s, corrente_a | RNF-03 |
| `disponibilidade_web.csv` | dia, minutos_fora_do_ar | RF-0010 |
| `servicos.csv` | camada, servico, status | RNF-0014 |
| `log_eventos.csv` | data; usuario; tipo; mensagem | RF-0013 (criado pelo sistema) |

### Valores de referência adotados

| Indicador | Regra |
|-----------|-------|
| Comunicação | até 200 ms = conectado, até 1000 ms = instável, acima disso = sem comunicação |
| Meta de produção | 800 peças por turno |
| Ciclo ideal | 30 segundos por peça |
| Taxa de acerto | meta de 95% |
| Processamento da câmera | até 200 ms por imagem |
| OEE | ≥ 85% classe mundial, ≥ 60% aceitável |
| Telemetria | uma atualização a cada 1000 ms, no máximo |
| Latência do vídeo | até 500 ms |
| Resposta da emergência | até 100 ms |
| Disponibilidade web | mínimo de 99,5% |

---

## Tecnologias e conceitos

- **Java 17+**
- **Maven**
- **VS Code** + Extension Pack for Java
- `Scanner`
- `BufferedReader` / `FileReader`
- `BufferedWriter` / `FileWriter` (modo append)
- Vetores (`String[]`, `int[]`, `long[]`)
- Matrizes (`int[][]`)
- `if / else`, `switch`, `for`, `while`, `do-while`
- Métodos `static` com e sem retorno
- `try / catch`
- `split()`, `trim()`, `equals()`, `contains()`
- `Integer.parseInt()` / `Double.parseDouble()`
- `printf` / `String.format`
- `LocalDateTime` (relógio do sistema)

---

## Como executar

### Requisitos

- **JDK 17** ou superior instalado.
- (Opcional) **VS Code** com a extensão *Extension Pack for Java*.

Para conferir a instalação:

```bash
java -version
javac -version
```

### Pelo VS Code

1. Abra a pasta **`demo`** no VS Code (*File > Open Folder*).
2. Abra `src/main/java/com/example/App.java`.
3. Clique em **Run**, acima do método `main`.

> A pasta aberta precisa ser `demo`, porque o programa lê os arquivos da pasta `dados/`.

### Pelo terminal

Entre na pasta do projeto:

```bash
cd demo
```

Compile:

```bash
javac -encoding UTF-8 -d target/classes src/main/java/com/example/*.java
```

Execute:

```bash
java -cp target/classes com.example.App
```

> Se o terminal mostrar códigos como `[92m` no lugar das cores, desligue as cores em
> **20 - Perfil > 3 - Ligar/desligar cores**.

---

## Estrutura

```
Auditando_Requisitos_em_Java/
├── README.md
└── demo/
    ├── pom.xml
    ├── dados/
    │   ├── comunicacao.csv
    │   ├── disponibilidade_web.csv
    │   ├── inspecoes.csv
    │   ├── latencia_video.csv
    │   ├── producao.csv
    │   ├── servicos.csv
    │   ├── telemetria.csv
    │   └── usuarios.csv
    └── src/main/java/com/example/
        ├── App.java            → login e menu principal (sidebar)
        ├── Entrada.java        → leitura do teclado
        ├── Tela.java           → cores, tema, logotipo, relógio e largura
        ├── Acesso.java         → login, perfis e logout
        ├── Log.java            → gravação e leitura de logs
        ├── Notificacoes.java   → central de notificações
        ├── Emergencia.java     → parada de emergência
        ├── Robo.java           → status do robô, programa e comunicação
        ├── Camera.java         → câmera, gravação, inspeção e latência
        ├── Producao.java       → peças, ciclo, OEE e disponibilidade
        └── SistemaWeb.java     → telemetria, disponibilidade web e arquitetura
```

---

## Objetivo

Projeto desenvolvido com fins acadêmicos para praticar **lógica de programação e algoritmos em Java**,
ligando os requisitos levantados na **auditoria de um sistema supervisório industrial** à sua implementação
em código, com rastreabilidade entre cada requisito e a função que o atende.
