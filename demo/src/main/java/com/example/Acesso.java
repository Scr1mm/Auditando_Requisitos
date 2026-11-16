package com.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Acesso {

    static final String ARQUIVO_USUARIOS = "dados/usuarios.csv";
    static final int MAXIMO_TENTATIVAS = 3;

    static String usuarioLogado = "";
    static String nomeLogado = "";
    static String perfilLogado = "";
    static String horaLogin = "";


    public static boolean fazerLogin() {
        int tentativas = 0;
        boolean logou = false;

        while (tentativas < MAXIMO_TENTATIVAS && !logou) {
            System.out.println();
            System.out.println("========== LOGIN ==========");
            String usuario = Entrada.lerTexto("Usuário: ");
            String senha = Entrada.lerTexto("Senha  : ");

            logou = validarUsuario(usuario, senha);

            if (logou) {
                horaLogin = Tela.dataHoraAtual();
                Log.registrar("ACESSO", "Login realizado com perfil " + perfilLogado);
                Notificacoes.adicionar("INFO", "Bem-vindo(a), " + nomeLogado + "!");
            } else {
                tentativas++;
                int restantes = MAXIMO_TENTATIVAS - tentativas;
                System.out.println(Tela.vermelho("Usuário ou senha incorretos. Tentativas restantes: " + restantes));
                Log.registrar("ACESSO", "Tentativa de login falhou para o usuário: " + usuario);
            }
        }

        return logou;
    }

   
    public static boolean validarUsuario(String usuario, String senha) {
        boolean encontrado = false;

        try {
            BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_USUARIOS));
            String linha;

            leitor.readLine(); 

            while ((linha = leitor.readLine()) != null) {
                String[] dados = linha.split(",");

                if (dados[0].equals(usuario) && dados[1].equals(senha)) {
                    usuarioLogado = dados[0];
                    perfilLogado = dados[2];
                    nomeLogado = dados[3];
                    encontrado = true;
                }
            }
            leitor.close();

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de usuários: " + e.getMessage());
        }

        return encontrado;
    }

    

    
    public static int nivelDoPerfil(String perfil) {
        int nivel;
        switch (perfil) {
            case "OPERADOR":
                nivel = 1;
                break;
            case "SUPERVISOR":
                nivel = 2;
                break;
            case "ADMIN":
                nivel = 3;
                break;
            default:
                nivel = 0;
        }
        return nivel;
    }

    
    public static boolean temPermissao(String perfilMinimo) {
        return nivelDoPerfil(perfilLogado) >= nivelDoPerfil(perfilMinimo);
    }


    public static void acessoNegado(String funcionalidade, String perfilMinimo) {
        System.out.println();
        System.out.println(Tela.vermelho("ACESSO NEGADO!"));
        System.out.println("A função \"" + funcionalidade + "\" exige perfil " + perfilMinimo + " ou superior.");
        Log.registrar("ACESSO", "Acesso negado a: " + funcionalidade);
        Entrada.pausar();
    }

 

    public static boolean menuPerfil() {
        int opcao;
        boolean fezLogout = false;

        do {
            Tela.cabecalho();
            Tela.titulo("RF-19 - Perfil do Usuário");

            System.out.println("Nome ........: " + nomeLogado);
            System.out.println("Usuário .....: " + usuarioLogado);
            System.out.println("Perfil ......: " + Tela.azul(perfilLogado));
            System.out.println("Login em ....: " + horaLogin);
            System.out.println();
            System.out.println("Permissões do perfil:");
            System.out.println(" - Monitorar robô, câmera e produção ... " + simOuNao(temPermissao("OPERADOR")));
            System.out.println(" - Rearmar emergência e ver logs ...... " + simOuNao(temPermissao("SUPERVISOR")));
            System.out.println(" - Arquitetura e disponibilidade web .. " + simOuNao(temPermissao("ADMIN")));
            System.out.println();

            String tema;
            if (Tela.modoEscuro) {
                tema = "ESCURO";
            } else {
                tema = "CLARO";
            }
            System.out.println("1 - Alternar tema (atual: " + tema + ")            [RNF-01]");
            System.out.println("2 - Ajustar largura da tela (atual: " + Tela.largura + ")   [RNF-05]");
            System.out.println("3 - Ligar/desligar cores");
            System.out.println("4 - Logout");
            System.out.println("0 - Voltar");
            opcao = Entrada.lerInteiro("Escolha: ");

            switch (opcao) {
                case 1:
                    Tela.alternarTema();
                    break;
                case 2:
                    Tela.ajustarLargura();
                    Entrada.pausar();
                    break;
                case 3:
                    Tela.usarCores = !Tela.usarCores;
                    Tela.resetarCores();
                    break;
                case 4:
                    Log.registrar("ACESSO", "Logout realizado");
                    System.out.println("Até logo, " + nomeLogado + "!");
                    usuarioLogado = "";
                    nomeLogado = "";
                    perfilLogado = "";
                    fezLogout = true;
                    opcao = 0; // sai do laço
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    Entrada.pausar();
            }
        } while (opcao != 0);

        return fezLogout;
    }

    public static String simOuNao(boolean valor) {
        if (valor) {
            return Tela.verde("SIM");
        } else {
            return Tela.vermelho("NÃO");
        }
    }
}
