package Level1;

import java.util.Scanner;

public class HolaMundo {
    public static void main(String[] args) {
        // Lector para pedir datos por teclado
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== REPASO: VARIABLES Y TECLADO ===");

        // 1. Texto (String)
        System.out.print("¿Cómo te llamas? ");
        String nombre = scanner.nextLine();

        // 2. Número entero (int)
        System.out.print("¿Cuántos años tienes? ");
        int edad = scanner.nextInt();

        // 3. Número con decimales (double)
        System.out.print("¿Cuánto te   gustaría cobrar de dev al mes (€)? ");
        double sueldoDeseado = scanner.nextDouble();

        // 4. Lógica básica (if/else)
        System.out.println("\n--- PERFIL GENERADO ---");
        System.out.println("Programador: " + nombre);
        System.out.println("Edad: " + edad + " años");
        System.out.println("Objetivo: " + sueldoDeseado + "€/mes");

        if (edad >= 18) {
            System.out.println("Estado: Mayor de edad. ¡A darle duro al código!");
        } else {
            System.out.println("Estado: Menor de edad. ¡Aprenderás rápido!");
        }

        scanner.close(); // Siempre es buena práctica cerrar el teclado
    }
}