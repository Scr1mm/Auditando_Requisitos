package com.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Robo {
    static String estadoRobo = "PARADO";
    static String modoOperacao = "AUTOMATICO";
    static int velocidade = 50; 
    static double[] posicaoEixos = {0.0, -45.0, 90.0, 0.0, 45.0, 0.0};
    static int[] temperaturaMotores = {38, 41, 45, 36, 52, 34};
    static final int TEMPERATURA_ATENCAO = 60;
    static final int TEMPERATURA_MAXIMA = 70;
    static String programaAtual = "PALETIZACAO_CX01";
    static String estadoPrograma = "AGUARDANDO";
    static String[] passosPrograma = {
        "Mover para posição HOME",
        "Aguardar peça na esteira",
        "Aproximar da peça",
        "Fechar garra",
        "Elevar peça",
        "Mover até o palete",
        "Posicionar peça",
        "Abrir garra",
        "Retornar para HOME"
    };
    static int passoAtual = 0;
    static int ciclosConcluidos = 0;
    static final String ARQUIVO_COMUNICACAO = "dados/comunicacao.csv";

    public static String estadoColorido(String estado) {
        switch (estado) {
            case "OPERANDO":
            case "EXECUTANDO":
            case "FINALIZADO":
            case "CONECTADO":
                return Tela.verde(estado);
            case "PARADO":
            case "AGUARDANDO":
            case "PAUSADO":
            case "INSTAVEL":
                return Tela.amarelo(estado);
            default: 
                return Tela.vermelho(estado);
        }
    }

    public static void menuStatusRobo() {
        int opcao;
        do {
            Tela.cabecalho();
            Tela.titulo("RF-02 - Status do Robô");

            System.out.println("Estado atual .....: " + estadoColorido(estadoRobo));
            System.out.println("Modo de operação .: " + modoOperacao);
            System.out.println("Velocidade .......: " + velocidade + "%");
            System.out.println("Ciclos concluídos : " + ciclosConcluidos);
            System.out.println();
            System.out.println("Eixo | Posição (graus) | Temperatura do motor");

            for (int i = 0; i < posicaoEixos.length; i++) {
                String temperatura = temperaturaMotores[i] + " °C";
                if (temperaturaMotores[i] > TEMPERATURA_MAXIMA) {
                    temperatura = Tela.vermelho(temperatura + " ALTA!");
                } else if (temperaturaMotores[i] > TEMPERATURA_ATENCAO) {
                    temperatura = Tela.amarelo(temperatura + " atenção");
                } else {
                    temperatura = Tela.verde(temperatura);
                }
                System.out.printf(" J%d  | %10.1f      | %s%n", (i + 1), posicaoEixos[i], temperatura);
            }

            System.out.println();
            System.out.println("1 - Iniciar operação");
            System.out.println("2 - Parar robô / reconhecer falha");
            System.out.println("3 - Simular falha (teste)");
            System.out.println("4 - Alterar velocidade");
            System.out.println("0 - Voltar");
            opcao = Entrada.lerInteiro("Escolha: ");

            switch (opcao) {
                case 1:
                    iniciarOperacao();
                    break;
                case 2:
                    pararRobo();
                    break;
                case 3:
                    simularFalha();
                    break;
                case 4:
                    alterarVelocidade();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    Entrada.pausar();
            }
        } while (opcao != 0);
    }

    public static void iniciarOperacao() {
        if (Emergencia.emergenciaAtiva) {
            System.out.println(Tela.vermelho("Não é possível iniciar: emergência ativa. Faça o rearme primeiro."));
        } else if (estadoRobo.equals("FALHA")) {
            System.out.println(Tela.vermelho("Robô em FALHA. Reconheça a falha (opção 2) antes de iniciar."));
        } else {
            estadoRobo = "OPERANDO";
            System.out.println(Tela.verde("Robô em operação."));
            Log.registrar("INFO", "Robô iniciado");
            Notificacoes.adicionar("INFO", "Robô iniciou a operação");
        }
        Entrada.pausar();
    }

    public static void pararRobo() {
        if (Emergencia.emergenciaAtiva) {
            System.out.println(Tela.vermelho("Emergência ativa. Use o rearme da emergência."));
        } else {
            if (estadoRobo.equals("FALHA")) {
                temperaturaMotores[4] = 52;
                Log.registrar("INFO", "Falha do robô reconhecida");
            }
            estadoRobo = "PARADO";
            if (estadoPrograma.equals("EXECUTANDO")) {
                estadoPrograma = "PAUSADO";
            }
            System.out.println(Tela.amarelo("Robô parado."));
            Log.registrar("INFO", "Robô parado pelo usuário");
        }
        Entrada.pausar();
    }

    public static void simularFalha() {
        if (Emergencia.emergenciaAtiva) {
            System.out.println("Emergência ativa. Não é possível simular falha agora.");
        } else {
            estadoRobo = "FALHA";
            temperaturaMotores[4] = 78;
            if (estadoPrograma.equals("EXECUTANDO")) {
                estadoPrograma = "PAUSADO";
            }
            System.out.println(Tela.vermelho("FALHA: superaquecimento no motor do eixo J5!"));
            Log.registrar("ALARME", "Falha no robô: superaquecimento J5");
            Notificacoes.adicionar("ALARME", "Falha no robô: superaquecimento J5");
        }
        Entrada.pausar();
    }

    public static void alterarVelocidade() {
        int nova = Entrada.lerInteiro("Nova velocidade (10 a 100%): ");
        if (nova < 10 || nova > 100) {
            System.out.println(Tela.vermelho("Velocidade inválida."));
        } else if (Emergencia.emergenciaAtiva) {
            System.out.println(Tela.vermelho("Emergência ativa. Velocidade travada em 0%."));
        } else {
            velocidade = nova;
            System.out.println("Velocidade alterada para " + velocidade + "%.");
            Log.registrar("CONFIG", "Velocidade alterada para " + velocidade + "%");
        }
        Entrada.pausar();
    }

    public static void menuStatusPrograma() {
        int opcao;
        do {
            Tela.cabecalho();
            Tela.titulo("RF-03 - Status do Programa");
            int totalPassos = passosPrograma.length;
            double progresso = (double) passoAtual * 100 / totalPassos;

            System.out.println("Programa ..........: " + programaAtual);
            System.out.println("Estado do programa : " + estadoColorido(estadoPrograma));
            System.out.println("Passo atual .......: " + (passoAtual + 1) + " de " + totalPassos
                    + " - " + passosPrograma[passoAtual]);
            System.out.printf("Progresso do ciclo: %s %.0f%%%n", Tela.barraProgresso(progresso, 20), progresso);
            System.out.println("Ciclos concluídos .: " + ciclosConcluidos);
            System.out.println();

            for (int i = 0; i < totalPassos; i++) {
                String marcador;
                if (i < passoAtual) {
                    marcador = Tela.verde("[OK]");
                } else if (i == passoAtual) {
                    marcador = Tela.amarelo("[>>]");
                } else {
                    marcador = "[  ]";
                }
                System.out.println(" " + marcador + " " + (i + 1) + ". " + passosPrograma[i]);
            }

            System.out.println();
            System.out.println("1 - Executar próximo passo");
            System.out.println("2 - Reiniciar programa");
            System.out.println("0 - Voltar");
            opcao = Entrada.lerInteiro("Escolha: ");

            if (opcao == 1) {
                executarProximoPasso();
            } else if (opcao == 2) {
                passoAtual = 0;
                estadoPrograma = "AGUARDANDO";
                Log.registrar("INFO", "Programa reiniciado");
            }
        } while (opcao != 0);
    }

    public static void executarProximoPasso() {
        if (!estadoRobo.equals("OPERANDO")) {
            System.out.println(Tela.vermelho("O robô precisa estar OPERANDO para executar o programa."));
            Entrada.pausar();
            return;
        }

        estadoPrograma = "EXECUTANDO";
        passoAtual++;

    
        if (passoAtual == passosPrograma.length) {
            ciclosConcluidos++;
            passoAtual = 0;
            estadoPrograma = "FINALIZADO";
            System.out.println(Tela.verde("Ciclo concluído! Total de ciclos: " + ciclosConcluidos));
            Notificacoes.adicionar("INFO", "Ciclo " + ciclosConcluidos + " do programa concluído");
            Log.registrar("INFO", "Ciclo do programa concluído");
            Entrada.pausar();
        }
    }

    public static void exibirStatusComunicacao() {
        Tela.cabecalho();
        Tela.titulo("RF-04 - Status da Comunicação");

        int conectados = 0;
        int instaveis = 0;
        int semComunicacao = 0;

        try {
            BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_COMUNICACAO));
            String linha;
            leitor.readLine();

            System.out.printf("%-22s %-15s %-10s %s%n", "DISPOSITIVO", "IP", "PING", "STATUS");

            while ((linha = leitor.readLine()) != null) {
                String[] dados = linha.split(",");
                String dispositivo = dados[0];
                String ip = dados[1];
                int ping = Integer.parseInt(dados[2].trim());

                String status;
                if (ping <= 200) {
                    status = "CONECTADO";
                    conectados++;
                } else if (ping <= 1000) {
                    status = "INSTAVEL";
                    instaveis++;
                } else {
                    status = "SEM COMUNICACAO";
                    semComunicacao++;
                    Notificacoes.adicionar("ALARME", "Sem comunicação com " + dispositivo);
                }

                System.out.printf("%-22s %-15s %-10s %s%n", dispositivo, ip, ping + " ms", estadoColorido(status));
            }
            leitor.close();

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de comunicação: " + e.getMessage());
        }

        Tela.linha('-');
        System.out.println("Conectados: " + Tela.verde("" + conectados)
                + "  |  Instáveis: " + Tela.amarelo("" + instaveis)
                + "  |  Sem comunicação: " + Tela.vermelho("" + semComunicacao));
        System.out.println("Regra: até 200 ms = conectado | até 1000 ms = instável | acima = sem comunicação");
        Entrada.pausar();
    }
}
