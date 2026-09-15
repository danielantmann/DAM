package Level1;

import java.util.Scanner;

public class Ej5 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numero = 0 ;

        int [] numeros = new int[5];

        double resultado = 0;

        for (int i = 0; i < 5 ; i++) {
            System.out.println("dime un numero");
            numero = scanner.nextInt();
            numeros[i] = numero;
            resultado += numero;
        }

        resultado  /= 5;
        System.out.println(resultado);
        scanner.close();
    }
}
