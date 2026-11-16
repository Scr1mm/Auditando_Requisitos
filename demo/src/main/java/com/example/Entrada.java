package com.example;
import java.util.Scanner;

public class Entrada {

    static Scanner teclado = new Scanner(System.in);
    public static String lerTexto(String pergunta) {
        System.out.print(pergunta);
        String texto = teclado.nextLine();
        return texto.trim();
    }

    public static int lerInteiro(String pergunta) {
        int numero = 0;
        boolean valido = false;

        do {
            String texto = lerTexto(pergunta);
            try {
                numero = Integer.parseInt(texto);
                valido = true;
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido! Digite apenas números inteiros.");
            }
        } while (!valido);

        return numero;
    }

    public static void pausar() {
        lerTexto("\nPressione ENTER para continuar...");
    }
}
