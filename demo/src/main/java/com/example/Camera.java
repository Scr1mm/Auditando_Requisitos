package com.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Camera {

    static final String ARQUIVO_INSPECOES = "dados/inspecoes.csv";
    static final String ARQUIVO_LATENCIA = "dados/latencia_video.csv";

    static final double META_TAXA_ACERTO = 95.0;   
    static final int TEMPO_MAXIMO_PROCESSAMENTO = 200; 
    static final int LATENCIA_MAXIMA_MS = 500;     
    static boolean gravando = false;
    static int quadrosGravados = 0;

    static final int LINHAS = 7;
    static final int COLUNAS = 30;
    static int[][] quadro = new int[LINHAS][COLUNAS];
    static int posicaoPeca = 2;
    static int numeroQuadro = 1;


    public static String indicadorGravacao() {
        if (gravando) {
            return Tela.vermelho("● REC");
        } else {
            return "○ sem gravação";
        }
    }

    public static void alternarGravacao() {
        gravando = !gravando;
        if (gravando) {
            Log.registrar("INFO", "Gravação de vídeo iniciada");
            Notificacoes.adicionar("INFO", "Gravação de vídeo iniciada");
        } else {
            Log.registrar("INFO", "Gravação de vídeo parada. Quadros gravados: " + quadrosGravados);
            Notificacoes.adicionar("INFO", "Gravação parada (" + quadrosGravados + " quadros)");
        }
    }

   
    public static void montarQuadro() {
        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                quadro[i][j] = 0;
            }
        }

        for (int j = 0; j < COLUNAS; j++) {
            quadro[5][j] = 1;
        }
    
        for (int i = 3; i <= 4; i++) {
            for (int j = posicaoPeca; j < posicaoPeca + 3 && j < COLUNAS; j++) {
                quadro[i][j] = 2;
            }
        }
    }

    public static void desenharQuadro() {
        System.out.print("+");
        for (int j = 0; j < COLUNAS; j++) {
            System.out.print("-");
        }
        System.out.println("+");

        for (int i = 0; i < LINHAS; i++) {
            System.out.print("|");
            for (int j = 0; j < COLUNAS; j++) {
                switch (quadro[i][j]) {
                    case 1:
                        System.out.print("=");
                        break;
                    case 2:
                        System.out.print("#");
                        break;
                    default:
                        System.out.print(" ");
                }
            }
            System.out.println("|");
        }

        System.out.print("+");
        for (int j = 0; j < COLUNAS; j++) {
            System.out.print("-");
        }
        System.out.println("+");
    }

    public static void menuCamera() {
        int opcao;
        do {
            montarQuadro();
            Tela.cabecalho();
            Tela.titulo("RF-08 - Visualização de Câmera/Vídeo");

            System.out.println("CAM-01 | Esteira de Inspeção | AO VIVO | Quadro nº " + numeroQuadro
                    + " | " + indicadorGravacao());
            desenharQuadro();

            if (posicaoPeca >= 12 && posicaoPeca <= 16) {
                System.out.println(Tela.amarelo("Peça na ZONA DE INSPEÇÃO"));
            } else {
                System.out.println("Aguardando peça na zona de inspeção...");
            }
            if (gravando) {
                System.out.println(Tela.vermelho("Gravando... quadros gravados: " + quadrosGravados));
            }

            System.out.println();
            System.out.println("1 - Próximo quadro (avançar vídeo)");
            if (gravando) {
                System.out.println("2 - PARAR gravação");
            } else {
                System.out.println("2 - INICIAR gravação");
            }
            System.out.println("0 - Voltar");
            opcao = Entrada.lerInteiro("Escolha: ");

            if (opcao == 1) {
                proximoQuadro();
            } else if (opcao == 2) {
                alternarGravacao();
            }
        } while (opcao != 0);
    }

    public static void proximoQuadro() {
        numeroQuadro++;
        posicaoPeca = posicaoPeca + 2;
        if (posicaoPeca >= COLUNAS) {
            posicaoPeca = 0; 
        }
        if (gravando) {
            quadrosGravados++;
        }
    }


    public static void exibirStatusProcessamento() {
        Tela.cabecalho();
        Tela.titulo("RF-10 - Status de Processamento da Câmera");

        int total = 0;
        int lentas = 0;
        int erros = 0;
        int somaTempo = 0;
        String ultimoResultado = "";
        String ultimaPeca = "";

        try {
            BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_INSPECOES));
            String linha;
            leitor.readLine(); 

            System.out.printf("%-8s %-10s %s%n", "PEÇA", "RESULTADO", "TEMPO");

            while ((linha = leitor.readLine()) != null) {
                String[] dados = linha.split(",");
                String peca = dados[0];
                String resultado = dados[1];
                int tempo = Integer.parseInt(dados[2].trim());

                total++;
                somaTempo = somaTempo + tempo;
                ultimaPeca = peca;
                ultimoResultado = resultado;

                String tempoTexto = tempo + " ms";
                if (tempo > TEMPO_MAXIMO_PROCESSAMENTO) {
                    tempoTexto = Tela.amarelo(tempoTexto + " (lento)");
                    lentas++;
                }

                String resultadoTexto;
                if (resultado.equals("OK")) {
                    resultadoTexto = Tela.verde("OK");
                } else if (resultado.equals("NOK")) {
                    resultadoTexto = Tela.amarelo("NOK");
                } else {
                    resultadoTexto = Tela.vermelho("ERRO");
                    erros++;
                }

                System.out.printf("%-8s %-10s %s%n", peca, resultadoTexto, tempoTexto);
            }
            leitor.close();

        } catch (IOException e) {
            System.out.println("Erro ao ler inspeções: " + e.getMessage());
            Entrada.pausar();
            return;
        }

        Tela.linha('-');
        if (total > 0) {
            double media = (double) somaTempo / total;
            System.out.printf("Imagens processadas: %d  |  Tempo médio: %.1f ms  |  Lentas: %d  |  Erros: %d%n",
                    total, media, lentas, erros);
        }

        System.out.print("STATUS DO PROCESSAMENTO: ");
        if (ultimoResultado.equals("ERRO")) {
            System.out.println(Tela.vermelho("FALHA ao processar a peça " + ultimaPeca));
        } else {
            System.out.println(Tela.verde("PROCESSANDO NORMALMENTE (última peça: " + ultimaPeca + ")"));
        }
        Entrada.pausar();
    }


    public static void exibirTaxaAcerto() {
        Tela.cabecalho();
        Tela.titulo("RF-07 - Indicador de Taxa de Acerto");

        int aprovadas = 0;
        int reprovadas = 0;
        int erros = 0;

        try {
            BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_INSPECOES));
            String linha;
            leitor.readLine();

            while ((linha = leitor.readLine()) != null) {
                String[] dados = linha.split(",");
                String resultado = dados[1];

                if (resultado.equals("OK")) {
                    aprovadas++;
                } else if (resultado.equals("NOK")) {
                    reprovadas++;
                } else {
                    erros++;
                }
            }
            leitor.close();

        } catch (IOException e) {
            System.out.println("Erro ao ler inspeções: " + e.getMessage());
            Entrada.pausar();
            return;
        }

        int total = aprovadas + reprovadas + erros;
        if (total == 0) {
            System.out.println("Nenhuma inspeção encontrada.");
            Entrada.pausar();
            return;
        }

        double taxa = (double) aprovadas * 100 / total;

        System.out.println("Peças inspecionadas : " + total);
        System.out.println("Aprovadas (OK) .....: " + aprovadas);
        System.out.println("Reprovadas (NOK) ...: " + reprovadas);
        System.out.println("Erros de leitura ...: " + erros);
        System.out.println();

        String taxaTexto = String.format("%.1f%%", taxa);
        System.out.println("TAXA DE ACERTO: " + Tela.barraProgresso(taxa, 25) + " "
                + Tela.corPorValor(taxa, META_TAXA_ACERTO, 85, taxaTexto));
        System.out.println("Meta: " + META_TAXA_ACERTO + "%");

        if (taxa >= META_TAXA_ACERTO) {
            System.out.println(Tela.verde("Meta atingida."));
        } else {
            System.out.println(Tela.vermelho("Abaixo da meta! Verifique a iluminação e o foco da câmera."));
            Notificacoes.adicionar("AVISO", "Taxa de acerto abaixo da meta: " + taxaTexto);
        }
        Entrada.pausar();
    }


    public static void exibirLatencia() {
        Tela.cabecalho();
        Tela.titulo("RNF-06 - Latência do Vídeo Streaming");

        int[] latencias = new int[100];
        int quantidade = 0;

        try {
            BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_LATENCIA));
            String linha;
            leitor.readLine();

            while ((linha = leitor.readLine()) != null && quantidade < latencias.length) {
                String[] dados = linha.split(",");
                latencias[quantidade] = Integer.parseInt(dados[1].trim());
                quantidade++;
            }
            leitor.close();

        } catch (IOException e) {
            System.out.println("Erro ao ler latências: " + e.getMessage());
            Entrada.pausar();
            return;
        }

        if (quantidade == 0) {
            System.out.println("Nenhuma amostra encontrada.");
            Entrada.pausar();
            return;
        }

        int soma = 0;
        int maior = latencias[0];
        int menor = latencias[0];
        int acimaDoLimite = 0;

        for (int i = 0; i < quantidade; i++) {
            soma = soma + latencias[i];
            if (latencias[i] > maior) {
                maior = latencias[i];
            }
            if (latencias[i] < menor) {
                menor = latencias[i];
            }

            String valor = latencias[i] + " ms";
            if (latencias[i] > LATENCIA_MAXIMA_MS) {
                acimaDoLimite++;
                valor = Tela.vermelho(valor + " ACIMA DO LIMITE");
            } else {
                valor = Tela.verde(valor);
            }
            System.out.println("Amostra " + (i + 1) + ": " + valor);
        }

        double media = (double) soma / quantidade;
        Tela.linha('-');
        System.out.printf("Média: %.1f ms  |  Menor: %d ms  |  Maior: %d ms  |  Limite: %d ms%n",
                media, menor, maior, LATENCIA_MAXIMA_MS);

        if (media <= LATENCIA_MAXIMA_MS && acimaDoLimite == 0) {
            System.out.println(Tela.verde("Latência CONFORME."));
        } else if (media <= LATENCIA_MAXIMA_MS) {
            System.out.println(Tela.amarelo("Média conforme, mas " + acimaDoLimite + " amostra(s) passaram do limite."));
        } else {
            System.out.println(Tela.vermelho("Latência NÃO CONFORME."));
        }
        Entrada.pausar();
    }
}
