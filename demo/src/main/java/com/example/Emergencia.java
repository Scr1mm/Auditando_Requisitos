package com.example;

public class Emergencia {

    static boolean emergenciaAtiva = false;
    static final long TEMPO_MAXIMO_MS = 100;
    static long[] temposResposta = new long[20];
    static int quantidadeAcionamentos = 0;
    public static void acionarParada() {
        
        long inicio = System.currentTimeMillis();

        emergenciaAtiva = true;
        Robo.estadoRobo = "EMERGENCIA";
        Robo.estadoPrograma = "ABORTADO";
        Robo.velocidade = 0;
        long fim = System.currentTimeMillis();
        long tempoResposta = fim - inicio;

       
        if (quantidadeAcionamentos < temposResposta.length) {
            temposResposta[quantidadeAcionamentos] = tempoResposta;
            quantidadeAcionamentos++;
        }

        Log.registrar("EMERGENCIA", "Parada de emergência acionada. Tempo de resposta: " + tempoResposta + " ms");
        Notificacoes.adicionar("ALARME", "PARADA DE EMERGÊNCIA ACIONADA!");

        Tela.cabecalho();
        exibirAlertaGrande();

        System.out.println("Tempo de resposta: " + tempoResposta + " ms (limite: " + TEMPO_MAXIMO_MS + " ms)");
        if (tempoResposta <= TEMPO_MAXIMO_MS) {
            System.out.println(Tela.verde("Tempo de resposta CONFORME (RNF-11)."));
        } else {
            System.out.println(Tela.vermelho("Tempo de resposta ACIMA do limite (RNF-11)!"));
        }
        Entrada.pausar();
    }

    public static void rearmar() {
        if (!Acesso.temPermissao("SUPERVISOR")) {
            Acesso.acessoNegado("Rearmar emergência", "SUPERVISOR");
            return;
        }

        Tela.cabecalho();
        Tela.titulo("Rearme da Emergência");

        if (!emergenciaAtiva) {
            System.out.println("Não existe emergência ativa.");
            Entrada.pausar();
            return;
        }

        String confirmacao = Entrada.lerTexto("A área do robô está segura? (S/N): ");
        if (confirmacao.equalsIgnoreCase("S")) {
            emergenciaAtiva = false;
            Robo.estadoRobo = "PARADO";
            Robo.estadoPrograma = "AGUARDANDO";
            Robo.passoAtual = 0;
            Robo.velocidade = 50;
            System.out.println(Tela.verde("Sistema rearmado. Robô PARADO e pronto para iniciar."));
            Log.registrar("EMERGENCIA", "Sistema rearmado");
            Notificacoes.adicionar("INFO", "Emergência rearmada");
        } else {
            System.out.println("Rearme cancelado.");
        }
        Entrada.pausar();
    }


    public static void exibirTemposResposta() {
        Tela.cabecalho();
        Tela.titulo("RNF-11 - Tempo de Resposta da Emergência");

        if (quantidadeAcionamentos == 0) {
            System.out.println("A emergência ainda não foi acionada nesta sessão.");
            Entrada.pausar();
            return;
        }

        long soma = 0;
        long maior = temposResposta[0];
        int foraDoLimite = 0;

        for (int i = 0; i < quantidadeAcionamentos; i++) {
            soma = soma + temposResposta[i];
            if (temposResposta[i] > maior) {
                maior = temposResposta[i];
            }
            if (temposResposta[i] > TEMPO_MAXIMO_MS) {
                foraDoLimite++;
            }
            System.out.println("Acionamento " + (i + 1) + ": " + temposResposta[i] + " ms");
        }

        double media = (double) soma / quantidadeAcionamentos;
        Tela.linha('-');
        System.out.printf("Média: %.2f ms  |  Maior: %d ms  |  Limite: %d ms%n", media, maior, TEMPO_MAXIMO_MS);

        if (foraDoLimite == 0) {
            System.out.println(Tela.verde("Todos os acionamentos dentro do limite."));
        } else {
            System.out.println(Tela.vermelho(foraDoLimite + " acionamento(s) acima do limite!"));
        }
        Entrada.pausar();
    }

    public static void exibirFaixaEmergencia() {
        String texto = " !!! EMERGÊNCIA ATIVA - ROBÔ PARADO - REARME NECESSÁRIO !!! ";
        System.out.println(Tela.destaqueVermelho(texto));
    }

 
    public static void exibirAlertaGrande() {
        String faixa = "";
        for (int i = 0; i < Tela.largura; i++) {
            faixa = faixa + " ";
        }
        String mensagem = "  >>>  PARADA DE EMERGÊNCIA ACIONADA  <<<";
        while (mensagem.length() < Tela.largura) {
            mensagem = mensagem + " ";
        }

        System.out.println();
        System.out.println(Tela.destaqueVermelho(faixa));
        System.out.println(Tela.destaqueVermelho(mensagem));
        System.out.println(Tela.destaqueVermelho(faixa));
        System.out.println();
    }
}
