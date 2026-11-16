package com.example;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Tela {

    static final String RESET = "\u001B[0m";
    static final String FUNDO_VERMELHO = "\u001B[101m\u001B[97m";
    static final String TEMA_ESCURO = "\u001B[40m\u001B[97m"; 
    static final String TEMA_CLARO = "\u001B[107m\u001B[30m"; 
    static boolean modoEscuro = true;  
    static boolean usarCores = true;  
    static int largura = 80;        

    public static String corDoTema() {
        if (!usarCores) {
            return "";
        }
        if (modoEscuro) {
            return TEMA_ESCURO;
        } else {
            return TEMA_CLARO;
        }
    }

    public static void aplicarTema() {
        System.out.print(corDoTema());
    }

    public static void alternarTema() {
        modoEscuro = !modoEscuro;
        if (modoEscuro) {
            System.out.println("Modo ESCURO ativado.");
        } else {
            System.out.println("Modo CLARO ativado.");
        }
        Log.registrar("CONFIG", "Tema alterado. Modo escuro = " + modoEscuro);
    }

    public static void resetarCores() {
        if (usarCores) {
            System.out.print(RESET);
        }
    }

    public static String pintar(String cor, String texto) {
        if (!usarCores) {
            return texto;
        }
        return cor + texto + RESET + corDoTema();
    }

    public static String verde(String texto) {

        return pintar(modoEscuro ? "\u001B[92m" : "\u001B[32m", texto);
    }

    public static String amarelo(String texto) {
        return pintar(modoEscuro ? "\u001B[93m" : "\u001B[33m", texto);
    }

    public static String vermelho(String texto) {
        return pintar(modoEscuro ? "\u001B[91m" : "\u001B[31m", texto);
    }

    public static String azul(String texto) {
        return pintar(modoEscuro ? "\u001B[96m" : "\u001B[34m", texto);
    }

    public static String destaqueVermelho(String texto) {
        return pintar(FUNDO_VERMELHO, texto);
    }

    public static String corPorValor(double valor, double limiteBom, double limiteAtencao, String texto) {
        if (valor >= limiteBom) {
            return verde(texto);
        } else if (valor >= limiteAtencao) {
            return amarelo(texto);
        } else {
            return vermelho(texto);
        }
    }

    public static void limpar() {
        if (usarCores) {
            aplicarTema();
            System.out.print("\u001B[H\u001B[2J");
            System.out.flush();
        } else {
            for (int i = 0; i < 3; i++) {
                System.out.println();
            }
        }
    }

    public static void linha(char simbolo) {
        for (int i = 0; i < largura; i++) {
            System.out.print(simbolo);
        }
        System.out.println();
    }

    public static void titulo(String texto) {
        linha('-');
        System.out.println("  " + azul(texto.toUpperCase()));
        linha('-');
    }

    public static String barraProgresso(double percentual, int tamanho) {
        int cheios = (int) (percentual * tamanho / 100);
        if (cheios > tamanho) {
            cheios = tamanho;
        }
        String barra = "[";
        for (int i = 0; i < tamanho; i++) {
            if (i < cheios) {
                barra = barra + "#";
            } else {
                barra = barra + "-";
            }
        }
        barra = barra + "]";
        return barra;
    }

    public static boolean telaPequena() {
        return largura < 70;
    }

    public static void ajustarLargura() {
        System.out.println("Largura atual: " + largura + " colunas.");
        int nova = Entrada.lerInteiro("Nova largura (40 a 150): ");
        if (nova < 40 || nova > 150) {
            System.out.println(vermelho("Valor fora do limite. Largura não alterada."));
        } else {
            largura = nova;
            if (telaPequena()) {
                System.out.println("Layout COMPACTO (1 coluna) ativado.");
            } else {
                System.out.println("Layout COMPLETO (2 colunas) ativado.");
            }
            Log.registrar("CONFIG", "Largura da tela alterada para " + largura);
        }
    }


    public static void logotipo() {
        if (telaPequena()) {
            System.out.println(azul("[SENAI]") + " Supervisório Industrial v1.0");
        } else {
            System.out.println(azul("  +-----------+"));
            System.out.println(azul("  | S E N A I |") + "   SISTEMA SUPERVISÓRIO INDUSTRIAL - v1.0");
            System.out.println(azul("  +-----------+") + "   Célula Robotizada de Produção - Linha 2");
        }
    }

    public static void cabecalho() {
        limpar();
        linha('=');
        logotipo();
        linha('=');

        System.out.println("Usuário: " + Acesso.nomeLogado + " (" + Acesso.perfilLogado + ")"
                + "   |   " + dataHoraAtual());
        String status = "Robô: " + Robo.estadoColorido(Robo.estadoRobo)
                + "  |  Câmera: " + Camera.indicadorGravacao()
                + "  |  Notificações: " + Notificacoes.total;
        System.out.println(status);
        if (Emergencia.emergenciaAtiva) {
            Emergencia.exibirFaixaEmergencia();
        }
        linha('=');
    }

    public static String dataHoraAtual() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return agora.format(formato);
    }

    public static String horaAtual() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        return agora.format(formato);
    }

    public static void exibirRelogio() {
        String resposta;
        do {
            cabecalho();
            titulo("RF-17 - Relógio do Sistema");

            LocalDateTime agora = LocalDateTime.now();
            int hora = agora.getHour();

            String turno;
            if (hora >= 6 && hora < 14) {
                turno = "1º Turno (06:00 às 14:00)";
            } else if (hora >= 14 && hora < 22) {
                turno = "2º Turno (14:00 às 22:00)";
            } else {
                turno = "3º Turno (22:00 às 06:00)";
            }

            System.out.println("Data e hora : " + azul(dataHoraAtual()));
            System.out.println("Turno atual : " + turno);
            System.out.println();
            resposta = Entrada.lerTexto("ENTER para atualizar o relógio ou 0 para voltar: ");
        } while (!resposta.equals("0"));
    }
}
