package Level1;

import java.util.Scanner;

public class Ej2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("dime un numero");
        int numero = scanner.nextInt();

        for (int i = 1; i <= numero; i++) {
            if ( (i % 2) == 0){
                System.out.println( i );
            }

        }
    scanner.close();
    }
}
