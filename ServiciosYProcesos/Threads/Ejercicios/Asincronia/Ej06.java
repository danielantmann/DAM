package Threads.Ejercicios.Asincronia;

import java.util.concurrent.CompletableFuture;

public class Ej06 {
    static void main() {
        CompletableFuture<Void> cadena = CompletableFuture
                .supplyAsync(()->10)
                .thenApply(num -> num *2 )
                .thenApply(num -> num +5)
                .thenApply(num -> String.valueOf(num))
                .thenAccept(texto -> System.out.println("Resultado: " + texto));

        cadena.join();
    }
}
