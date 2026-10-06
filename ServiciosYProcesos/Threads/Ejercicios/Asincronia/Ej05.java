package Threads.Ejercicios.Asincronia;

import java.util.concurrent.CompletableFuture;

public class Ej05 {
    static void main() {
        CompletableFuture<Void> reusultado = CompletableFuture.supplyAsync(()->{
            return 10 + 20;
        })
                .thenApply(num -> num * 2)
                .thenAccept(res -> System.out.println("Resultado: " + res));

        reusultado.join();
    }
}
