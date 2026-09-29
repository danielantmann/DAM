package Level1;

import java.util.Scanner;

public class Ej1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce un número: ");
        int numero = scanner.nextInt();

        if ( numero > 0){
            System.out.println("positivo");
        } else if (numero <0) {
            System.out.println("negativo");
        }else {
            System.out.println("cero");
        }

        System.out.println("Dime un numero:");
        int numeroMultiplicar = scanner.nextInt();
        for (int i = 1; i <= 10 ; i++) {
            System.out.println( numeroMultiplicar + " x " + i + " = " + (numeroMultiplicar * i));
        }
    }
}
