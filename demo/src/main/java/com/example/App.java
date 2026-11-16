package com.example;


public class App {

  
    static String[] itensMenu = {
        "1  - Status do Robô",
        "2  - Status do Programa",
        "3  - Status da Comunicação",
        "4  - Câmera ao Vivo / Gravação",
        "5  - Processamento da Câmera",
        "6  - Taxa de Acerto",
        "7  - Latência do Vídeo",
        "8  - Painel de Peças",
        "9  - Contador de Peças",
        "10 - Tempo de Ciclo",
        "11 - Eficiência (OEE)",
        "12 - Disponibilidade Mecânica",
        "13 - Telemetria",
        "14 - Central de Notificações",
        "15 - Relógio do Sistema",
        "16 - Tempo de Resp. Emergência",
        "17 - Logs do Sistema       [SUP]",
        "18 - Disponibilidade Web   [ADM]",
        "19 - Arquitetura Web       [ADM]",
        "20 - Perfil / Tema / Logout",
        "99 - PARADA DE EMERGÊNCIA",
        "98 - Rearmar emergência    [SUP]",
        "0  - Sair do sistema"
    };

    public static void main(String[] args) {
        boolean sairDoSistema = false;

        while (!sairDoSistema) {
            Tela.limpar();
            Tela.linha('=');
            Tela.logotipo();
            Tela.linha('=');
            System.out.println(Tela.dataHoraAtual());

            boolean logou = Acesso.fazerLogin();

            if (!logou) {
                System.out.println(Tela.vermelho("Número máximo de tentativas atingido. Sistema bloqueado."));
                sairDoSistema = true;
            } else {
                sairDoSistema = menuPrincipal();
            }
        }

        Log.registrar("INFO", "Sistema encerrado");
        Tela.resetarCores();
        System.out.println("Sistema encerrado.");
    }

   
    public static void exibirSidebar() {
        Tela.titulo("Menu de Navegação");

        if (Tela.telaPequena()) {
           
            for (int i = 0; i < itensMenu.length; i++) {
                imprimirItem(itensMenu[i]);
                System.out.println();
            }
        } else {
            
            for (int i = 0; i < itensMenu.length; i = i + 2) {
                imprimirItem(String.format("%-38s", itensMenu[i]));
                if (i + 1 < itensMenu.length) {
                    imprimirItem(itensMenu[i + 1]);
                }
                System.out.println();
            }
        }
        Tela.linha('-');
    }

 
    public static void imprimirItem(String item) {
        if (item.contains("EMERGÊNCIA")) {
            System.out.print(Tela.vermelho(item));
        } else {
            System.out.print(item);
        }
    }


    public static boolean menuPrincipal() {
        int opcao;

        do {
            Tela.cabecalho();
            exibirSidebar();
            opcao = Entrada.lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    Robo.menuStatusRobo();
                    break;
                case 2:
                    Robo.menuStatusPrograma();
                    break;
                case 3:
                    Robo.exibirStatusComunicacao();
                    break;
                case 4:
                    Camera.menuCamera();
                    break;
                case 5:
                    Camera.exibirStatusProcessamento();
                    break;
                case 6:
                    Camera.exibirTaxaAcerto();
                    break;
                case 7:
                    Camera.exibirLatencia();
                    break;
                case 8:
                    Producao.exibirPainel();
                    break;
                case 9:
                    Producao.exibirContador();
                    break;
                case 10:
                    Producao.exibirTempoCiclo();
                    break;
                case 11:
                    Producao.exibirOEE();
                    break;
                case 12:
                    Producao.exibirDisponibilidade();
                    break;
                case 13:
                    SistemaWeb.exibirTelemetria();
                    break;
                case 14:
                    Notificacoes.menuNotificacoes();
                    break;
                case 15:
                    Tela.exibirRelogio();
                    break;
                case 16:
                    Emergencia.exibirTemposResposta();
                    break;
                case 17:
                    if (Acesso.temPermissao("SUPERVISOR")) {
                        Log.exibirLogs();
                    } else {
                        Acesso.acessoNegado("Logs do Sistema", "SUPERVISOR");
                    }
                    break;
                case 18:
                    if (Acesso.temPermissao("ADMIN")) {
                        SistemaWeb.exibirDisponibilidadeWeb();
                    } else {
                        Acesso.acessoNegado("Disponibilidade Web", "ADMIN");
                    }
                    break;
                case 19:
                    if (Acesso.temPermissao("ADMIN")) {
                        SistemaWeb.exibirArquitetura();
                    } else {
                        Acesso.acessoNegado("Arquitetura Web", "ADMIN");
                    }
                    break;
                case 20:
                    boolean fezLogout = Acesso.menuPerfil();
                    if (fezLogout) {
                        return false; 
                    }
                    break;
                case 99:
                    Emergencia.acionarParada();
                    break;
                case 98:
                    Emergencia.rearmar();
                    break;
                case 0:
                    break;
                default:
                    System.out.println(Tela.vermelho("Opção inválida!"));
                    Entrada.pausar();
            }
        } while (opcao != 0);

        return true;
    }
}
