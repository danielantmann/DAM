package Threads.Ejercicios.SimulacroExam;

import java.util.concurrent.CompletableFuture;

//Cadena de procesamiento — 15 minutos (1,5 puntos)
//Crea una operación asíncrona que devuelva el número 8.
//A continuación:
//        1. Multiplícalo por tres.
//2. Réstale cuatro.
//        3. Convierte el resultado a String.
//4. Muéstralo por consola.
//        Utiliza supplyAsync(), thenApply() y thenAccept().
//Resultado:
//Resultado: 20
public class CadenaProcesamiento {
    static void main() {
        CompletableFuture<Void> resultado = CompletableFuture.supplyAsync(()->{
           return 8;
        })
                .thenApply(num -> num *3)
                .thenApply(num-> num -4)
                .thenApply(num -> String.valueOf(num))
                .thenAccept(txt -> System.out.println("Resultado: " + txt));
        resultado.join();
    }
}
