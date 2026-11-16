package com.example;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Log {

    static final String ARQUIVO_LOG = "dados/log_eventos.csv";
    public static void registrar(String tipo, String mensagem) {
        String usuario = Acesso.usuarioLogado;
        if (usuario.equals("")) {
            usuario = "-";
        }

        try {
            BufferedWriter escritor = new BufferedWriter(new FileWriter(ARQUIVO_LOG, true));
            escritor.write(Tela.dataHoraAtual() + ";" + usuario + ";" + tipo + ";" + mensagem);
            escritor.newLine();
            escritor.close();
        } catch (IOException e) {
            System.out.println("Erro ao gravar log: " + e.getMessage());
        }
    }


    public static void exibirLogs() {
        Tela.cabecalho();
        Tela.titulo("RF-0013 - Persistência de Logs");

        File arquivo = new File(ARQUIVO_LOG);
        if (!arquivo.exists()) {
            System.out.println("Nenhum log registrado ainda.");
            Entrada.pausar();
            return;
        }

        int total = 0;
        int alarmes = 0;
        int acessos = 0;
        int outros = 0;

        try {
            BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO_LOG));
            String linha;

            while ((linha = leitor.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length < 4) {
                    continue; 
                }
                String data = dados[0];
                String usuario = dados[1];
                String tipo = dados[2];
                String mensagem = dados[3];
                String tipoColorido;
                if (tipo.equals("EMERGENCIA") || tipo.equals("ALARME")) {
                    tipoColorido = Tela.vermelho(tipo);
                    alarmes++;
                } else if (tipo.equals("ACESSO")) {
                    tipoColorido = Tela.azul(tipo);
                    acessos++;
                } else {
                    tipoColorido = Tela.verde(tipo);
                    outros++;
                }

                System.out.println(data + " | " + usuario + " | " + tipoColorido + " | " + mensagem);
                total++;
            }
            leitor.close();

        } catch (IOException e) {
            System.out.println("Erro ao ler o log: " + e.getMessage());
        }

        Tela.linha('-');
        System.out.println("Total de eventos: " + total
                + "  |  Alarmes/Emergências: " + alarmes
                + "  |  Acessos: " + acessos
                + "  |  Outros: " + outros);
        Entrada.pausar();
    }
}
