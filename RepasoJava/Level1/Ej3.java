package Level1;

import java.util.Scanner;

public class Ej3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("dime un numero");

        int numero = scanner.nextInt();
        int resultado = 0;

        for (int i = 1; i <= numero ; i++) {
            resultado += i;
        }

        System.out.println(resultado);
        scanner.close();
    }

}
