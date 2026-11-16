package com.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Producao {

    static final String ARQUIVO_PRODUCAO = "dados/producao.csv";

    static final int CICLO_IDEAL_SEGUNDOS = 30;  
    static final int META_PECAS_TURNO = 800;
    static final int MINUTOS_POR_HORA = 60;
    static String[] horas = new String[24];
    static int[][] valores = new int[24][4];
    static int quantidadeHoras = 0;
    public static boolean carregarDados() {
        quantidadeHoras = 0;
        try {
            BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_PRODUCAO));
            String linha;
            leitor.readLine(); 

            while ((linha = leitor.readLine()) != null && quantidadeHoras < horas.length) {
                String[] dados = linha.split(",");
                horas[quantidadeHoras] = dados[0];
                for (int c = 0; c < 4; c++) {
                    valores[quantidadeHoras][c] = Integer.parseInt(dados[c + 1].trim());
                }
                quantidadeHoras++;
            }
            leitor.close();

        } catch (IOException e) {
            System.out.println("Erro ao ler a produção: " + e.getMessage());
            Entrada.pausar();
            return false;
        }

        if (quantidadeHoras == 0) {
            System.out.println("Arquivo de produção vazio.");
            Entrada.pausar();
            return false;
        }
        return true;
    }

    public static int somarColuna(int coluna) {
        int soma = 0;
        for (int i = 0; i < quantidadeHoras; i++) {
            soma = soma + valores[i][coluna];
        }
        return soma;
    }

    public static void exibirContador() {
        if (!carregarDados()) {
            return;
        }
        Tela.cabecalho();
        Tela.titulo("RF-05 - Contador de Peças Processadas");

        int boas = somarColuna(0);
        int rejeitadas = somarColuna(1);
        int total = boas + rejeitadas;
        double percentualMeta = (double) total * 100 / META_PECAS_TURNO;

        System.out.println("Peças processadas ..: " + Tela.azul("" + total));
        System.out.println("Peças boas .........: " + Tela.verde("" + boas));
        System.out.println("Peças rejeitadas ...: " + Tela.vermelho("" + rejeitadas));
        System.out.println();
        System.out.printf("Meta do turno (%d peças): %s %.1f%%%n",
                META_PECAS_TURNO, Tela.barraProgresso(percentualMeta, 25), percentualMeta);

        if (total >= META_PECAS_TURNO) {
            System.out.println(Tela.verde("Meta do turno atingida!"));
        } else {
            System.out.println("Faltam " + (META_PECAS_TURNO - total) + " peças para a meta.");
        }
        Entrada.pausar();
    }

    public static void exibirTempoCiclo() {
        if (!carregarDados()) {
            return;
        }
        Tela.cabecalho();
        Tela.titulo("RF-06 - Medição do Tempo de Ciclo");

        int menor = valores[0][2];
        int maior = valores[0][2];
        int soma = 0;
        double limiteAtencao = CICLO_IDEAL_SEGUNDOS * 1.10;

        for (int i = 0; i < quantidadeHoras; i++) {
            int ciclo = valores[i][2];
            soma = soma + ciclo;
            if (ciclo < menor) {
                menor = ciclo;
            }
            if (ciclo > maior) {
                maior = ciclo;
            }

            String texto = ciclo + " s";
            if (ciclo <= CICLO_IDEAL_SEGUNDOS) {
                texto = Tela.verde(texto);
            } else if (ciclo <= limiteAtencao) {
                texto = Tela.amarelo(texto);
            } else {
                texto = Tela.vermelho(texto + " (lento)");
            }
            System.out.println(horas[i] + "  ->  ciclo médio: " + texto);
        }

        double media = (double) soma / quantidadeHoras;
        Tela.linha('-');
        System.out.printf("Ciclo médio do turno: %.1f s  |  Menor: %d s  |  Maior: %d s  |  Ideal: %d s%n",
                media, menor, maior, CICLO_IDEAL_SEGUNDOS);

        double diferenca = media - CICLO_IDEAL_SEGUNDOS;
        if (diferenca <= 0) {
            System.out.println(Tela.verde("Ciclo dentro do ideal."));
        } else {
            System.out.printf("O ciclo está %.1f s acima do ideal.%n", diferenca);
        }
        Entrada.pausar();
    }

    public static double calcularDisponibilidade() {
        int tempoPlanejado = quantidadeHoras * MINUTOS_POR_HORA;
        int tempoParado = somarColuna(3);
        int tempoOperando = tempoPlanejado - tempoParado;
        return (double) tempoOperando * 100 / tempoPlanejado;
    }

    public static void exibirDisponibilidade() {
        if (!carregarDados()) {
            return;
        }
        Tela.cabecalho();
        Tela.titulo("RF-13 - Métricas de Disponibilidade Mecânica");
        int tempoPlanejado = quantidadeHoras * MINUTOS_POR_HORA;
        int tempoParado = somarColuna(3);
        int tempoOperando = tempoPlanejado - tempoParado;
        int numeroParadas = 0;
        for (int i = 0; i < quantidadeHoras; i++) {
            if (valores[i][3] > 0) {
                numeroParadas++;
            }
        }

        double disponibilidade = calcularDisponibilidade();

        System.out.println("Tempo planejado ....: " + tempoPlanejado + " min");
        System.out.println("Tempo operando .....: " + tempoOperando + " min");
        System.out.println("Tempo parado .......: " + tempoParado + " min");
        System.out.println("Número de paradas ..: " + numeroParadas);

        if (numeroParadas > 0) {
            double mtbf = (double) tempoOperando / numeroParadas;
            double mttr = (double) tempoParado / numeroParadas;
            System.out.printf("MTBF (tempo médio entre falhas): %.1f min%n", mtbf);
            System.out.printf("MTTR (tempo médio de reparo) ..: %.1f min%n", mttr);
        }

        System.out.println();
        String texto = String.format("%.1f%%", disponibilidade);
        System.out.println("DISPONIBILIDADE: " + Tela.barraProgresso(disponibilidade, 25) + " "
                + Tela.corPorValor(disponibilidade, 90, 80, texto));
        Entrada.pausar();
    }

    public static void exibirOEE() {
        if (!carregarDados()) {
            return;
        }
        Tela.cabecalho();
        Tela.titulo("RF-12 - Monitoramento da Eficiência (OEE)");

        int boas = somarColuna(0);
        int total = boas + somarColuna(1);
        int tempoOperandoSegundos = (quantidadeHoras * MINUTOS_POR_HORA - somarColuna(3)) * 60;
        double disponibilidade = calcularDisponibilidade();
        double desempenho = (double) CICLO_IDEAL_SEGUNDOS * total * 100 / tempoOperandoSegundos;
        if (desempenho > 100) {
            desempenho = 100;
        }
        double qualidade = (double) boas * 100 / total;
        double oee = (disponibilidade / 100) * (desempenho / 100) * (qualidade / 100) * 100;
        System.out.printf("Disponibilidade: %s %.1f%%%n", Tela.barraProgresso(disponibilidade, 20), disponibilidade);
        System.out.printf("Desempenho ....: %s %.1f%%%n", Tela.barraProgresso(desempenho, 20), desempenho);
        System.out.printf("Qualidade .....: %s %.1f%%%n", Tela.barraProgresso(qualidade, 20), qualidade);
        Tela.linha('-');
        String oeeTexto = String.format("%.1f%%", oee);
        System.out.println("OEE: " + Tela.barraProgresso(oee, 20) + " " + Tela.corPorValor(oee, 85, 60, oeeTexto));


        if (oee >= 85) {
            System.out.println(Tela.verde("Classificação: CLASSE MUNDIAL"));
        } else if (oee >= 60) {
            System.out.println(Tela.amarelo("Classificação: ACEITÁVEL - há espaço para melhoria"));
        } else {
            System.out.println(Tela.vermelho("Classificação: BAIXA - investigar perdas"));
            Notificacoes.adicionar("AVISO", "OEE baixo: " + oeeTexto);
        }
        Entrada.pausar();
    }

    public static void exibirPainel() {
        if (!carregarDados()) {
            return;
        }
        Tela.cabecalho();
        Tela.titulo("RF-14 - Painel de Monitoramento de Peças");

        System.out.printf("%-7s %6s %8s %7s %8s  %s%n", "HORA", "BOAS", "REJEIT.", "CICLO", "PARADO", "QUALIDADE");

        for (int i = 0; i < quantidadeHoras; i++) {
            int boas = valores[i][0];
            int rejeitadas = valores[i][1];
            int total = boas + rejeitadas;
            double qualidade = 0;
            if (total > 0) {
                qualidade = (double) boas * 100 / total;
            }
            String qualidadeTexto = String.format("%.1f%%", qualidade);

            System.out.printf("%-7s %6d %8d %6ds %6dmin  %s%n",
                    horas[i], boas, rejeitadas, valores[i][2], valores[i][3],
                    Tela.corPorValor(qualidade, 97, 93, qualidadeTexto));
        }

        Tela.linha('-');
        int totalBoas = somarColuna(0);
        int totalRejeitadas = somarColuna(1);
        double qualidadeTurno = (double) totalBoas * 100 / (totalBoas + totalRejeitadas);
        System.out.printf("%-7s %6d %8d %7s %6dmin  %.1f%%%n",
                "TOTAL", totalBoas, totalRejeitadas, "", somarColuna(3), qualidadeTurno);

        int horaPior = 0;
        for (int i = 1; i < quantidadeHoras; i++) {
            if (valores[i][1] > valores[horaPior][1]) {
                horaPior = i;
            }
        }
        System.out.println();
        System.out.println("Hora com mais rejeições: " + Tela.amarelo(horas[horaPior]
                + " (" + valores[horaPior][1] + " peças)"));
        System.out.println("Legenda: " + Tela.verde("verde >= 97%") + " | "
                + Tela.amarelo("amarelo >= 93%") + " | " + Tela.vermelho("vermelho < 93%"));
        Entrada.pausar();
    }
}
