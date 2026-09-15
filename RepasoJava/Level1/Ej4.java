package Level1;

import java.util.Scanner;

public class Ej4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("dime un numero");
        int numero = scanner.nextInt();

        boolean primo = esPrimo(numero);

        if (primo){
            System.out.println("es primo");
        }else {
            System.out.println("no es primo");
        }
    }

    public  static boolean esPrimo( int numero){
        if ( numero <= 1){
            return  false;
        }
        for (int i = 2; i < numero ; i++) {
            if (numero % i == 0){
                return false;
            }
        }
        return  true;
    }
}
