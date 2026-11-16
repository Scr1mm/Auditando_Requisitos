package com.example;

public class Notificacoes {

    static String[] mensagens = new String[10];
    static int total = 0;
    public static void adicionar(String tipo, String texto) {
        String mensagem = Tela.horaAtual() + " [" + tipo + "] " + texto;

        if (total == mensagens.length) {
            for (int i = 0; i < total - 1; i++) {
                mensagens[i] = mensagens[i + 1];
            }
            total--;
        }

        mensagens[total] = mensagem;
        total++;
    }

    public static void menuNotificacoes() {
        int opcao;
        do {
            Tela.cabecalho();
            Tela.titulo("RF-18 - Central de Notificações");

            if (total == 0) {
                System.out.println("Nenhuma notificação.");
            } else {
                int alarmes = 0;
                int avisos = 0;
                
                for (int i = total - 1; i >= 0; i--) {
                    String msg = mensagens[i];
                    if (msg.contains("[ALARME]")) {
                        System.out.println(Tela.vermelho(msg));
                        alarmes++;
                    } else if (msg.contains("[AVISO]")) {
                        System.out.println(Tela.amarelo(msg));
                        avisos++;
                    } else {
                        System.out.println(Tela.verde(msg));
                    }
                }
                Tela.linha('-');
                System.out.println("Total: " + total + "  |  Alarmes: " + alarmes + "  |  Avisos: " + avisos);
            }

            System.out.println();
            System.out.println("1 - Limpar notificações");
            System.out.println("0 - Voltar");
            opcao = Entrada.lerInteiro("Escolha: ");

            if (opcao == 1) {
                for (int i = 0; i < mensagens.length; i++) {
                    mensagens[i] = null;
                }
                total = 0;
                Log.registrar("INFO", "Notificações limpas");
            }
        } while (opcao != 0);
    }
}
