package com.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SistemaWeb {

    static final String ARQUIVO_TELEMETRIA = "dados/telemetria.csv";
    static final String ARQUIVO_DISPONIBILIDADE = "dados/disponibilidade_web.csv";
    static final String ARQUIVO_SERVICOS = "dados/servicos.csv";
    static final int INTERVALO_MAXIMO_MS = 1000;   
    static final double META_DISPONIBILIDADE = 99.5; 
    static final int MINUTOS_POR_DIA = 1440;

    public static void exibirTelemetria() {
        Tela.cabecalho();
        Tela.titulo("RNF-03 - Atualização de Telemetria");
        int atualizacoes = 0;
        int atrasadas = 0;
        int tempoAnterior = -1;

        try {
            BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_TELEMETRIA));
            String linha;
            leitor.readLine();

            System.out.printf("%-4s %-9s %-11s %-10s %-9s %s%n",
                    "Nº", "TEMPO", "TEMP.(°C)", "VIBRAÇÃO", "CORRENTE", "INTERVALO");

            while ((linha = leitor.readLine()) != null) {
                String[] dados = linha.split(",");
                int tempo = Integer.parseInt(dados[0].trim());
                double temperatura = Double.parseDouble(dados[1].trim());
                double vibracao = Double.parseDouble(dados[2].trim());
                double corrente = Double.parseDouble(dados[3].trim());
                atualizacoes++;
                String intervaloTexto = "-";
                if (tempoAnterior >= 0) {
                    int intervalo = tempo - tempoAnterior;
                    if (intervalo <= INTERVALO_MAXIMO_MS) {
                        intervaloTexto = Tela.verde(intervalo + " ms");
                    } else {
                        intervaloTexto = Tela.vermelho(intervalo + " ms ATRASO");
                        atrasadas++;
                    }
                }
                tempoAnterior = tempo;

                String temperaturaTexto = String.format("%.1f", temperatura);
                if (temperatura > 70) {
                    temperaturaTexto = Tela.vermelho(temperaturaTexto);
                }

                System.out.printf("%-4d %-9s %-11s %-10.2f %-9.1f %s%n",
                        atualizacoes, tempo + "ms", temperaturaTexto, vibracao, corrente, intervaloTexto);
            }
            leitor.close();

        } catch (IOException e) {
            System.out.println("Erro ao ler telemetria: " + e.getMessage());
            Entrada.pausar();
            return;
        }

        Tela.linha('-');
        System.out.println("Atualizações recebidas: " + atualizacoes
                + "  |  Intervalo máximo permitido: " + INTERVALO_MAXIMO_MS + " ms");
        if (atrasadas == 0) {
            System.out.println(Tela.verde("Telemetria CONFORME: todas as atualizações dentro do intervalo."));
        } else {
            System.out.println(Tela.vermelho(atrasadas + " atualização(ões) atrasada(s)!"));
            Notificacoes.adicionar("AVISO", "Telemetria com " + atrasadas + " atraso(s)");
        }
        Entrada.pausar();
    }

    public static void exibirDisponibilidadeWeb() {
        Tela.cabecalho();
        Tela.titulo("RNF-0010 - Disponibilidade do Web App");

        int dias = 0;
        int minutosFora = 0;
        int piorDia = 0;
        int piorMinutos = 0;

        try {
            BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_DISPONIBILIDADE));
            String linha;
            leitor.readLine();

            while ((linha = leitor.readLine()) != null) {
                String[] dados = linha.split(",");
                int dia = Integer.parseInt(dados[0].trim());
                int minutos = Integer.parseInt(dados[1].trim());

                dias++;
                minutosFora = minutosFora + minutos;
                if (minutos > piorMinutos) {
                    piorMinutos = minutos;
                    piorDia = dia;
                }
            }
            leitor.close();

        } catch (IOException e) {
            System.out.println("Erro ao ler disponibilidade: " + e.getMessage());
            Entrada.pausar();
            return;
        }

        if (dias == 0) {
            System.out.println("Nenhum dado encontrado.");
            Entrada.pausar();
            return;
        }

        int minutosTotais = dias * MINUTOS_POR_DIA;
        double disponibilidade = (double) (minutosTotais - minutosFora) * 100 / minutosTotais;

        System.out.println("Dias analisados .....: " + dias);
        System.out.println("Minutos no período ..: " + minutosTotais);
        System.out.println("Minutos fora do ar ..: " + minutosFora);
        if (piorMinutos > 0) {
            System.out.println("Pior dia ............: dia " + piorDia + " (" + piorMinutos + " min fora)");
        }
        System.out.println();

        String texto = String.format("%.3f%%", disponibilidade);
        System.out.println("DISPONIBILIDADE: " + Tela.corPorValor(disponibilidade, META_DISPONIBILIDADE, 99.0, texto)
                + "  (meta: " + META_DISPONIBILIDADE + "%)");

        if (disponibilidade >= META_DISPONIBILIDADE) {
            System.out.println(Tela.verde("Disponibilidade CONFORME."));
        } else {
            System.out.println(Tela.vermelho("Disponibilidade abaixo da meta!"));
        }
        Entrada.pausar();
    }

    public static void exibirArquitetura() {
        Tela.cabecalho();
        Tela.titulo("RNF-0014 - Arquitetura Web");

        System.out.println("  [Navegador / Tablet / Celular]");
        System.out.println("               |  HTTP");
        System.out.println("     [Servidor Web - Front-end]");
        System.out.println("               |  API");
        System.out.println("     [Servidor de Aplicação]---[Banco de Dados / Logs]");
        System.out.println("               |  Rede industrial");
        System.out.println("     [CLP]   [Robô]   [Câmera]");
        System.out.println();

        int online = 0;
        int offline = 0;

        try {
            BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_SERVICOS));
            String linha;
            leitor.readLine();

            System.out.printf("%-14s %-26s %s%n", "CAMADA", "SERVIÇO", "STATUS");

            while ((linha = leitor.readLine()) != null) {
                String[] dados = linha.split(",");
                String camada = dados[0];
                String servico = dados[1];
                String status = dados[2].trim().toUpperCase();

                String statusTexto;
                if (status.equals("ONLINE")) {
                    statusTexto = Tela.verde(status);
                    online++;
                } else {
                    statusTexto = Tela.vermelho(status);
                    offline++;
                }
                System.out.printf("%-14s %-26s %s%n", camada, servico, statusTexto);
            }
            leitor.close();

        } catch (IOException e) {
            System.out.println("Erro ao ler serviços: " + e.getMessage());
        }

        Tela.linha('-');
        System.out.println("Serviços online: " + online + "  |  offline: " + offline);
        if (offline > 0) {
            System.out.println(Tela.vermelho("Atenção: existem serviços fora do ar."));
        } else {
            System.out.println(Tela.verde("Todas as camadas funcionando."));
        }
        Entrada.pausar();
    }
}
