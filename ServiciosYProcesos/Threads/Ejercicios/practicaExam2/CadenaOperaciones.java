package Threads.Ejercicios.practicaExam2;
//Cadena de operaciones — 20 minutos (1,5 puntos)
//Construye una cadena utilizando CompletableFuture que realice estas operaciones:
//        10
//        ↓
//multiplicar por 2
//        ↓
//sumar 5
//        ↓
//convertir a String
// ↓
//mostrar
//
//
//Utiliza:
//        - supplyAsync()
//- thenApply()
//- thenAccept()
//- join() para esperar al resultado final.

import java.util.concurrent.CompletableFuture;

public class CadenaOperaciones {
 static void main() {
  CompletableFuture<Void> cadena = CompletableFuture.supplyAsync(()->10)
          .thenApply(num -> num * 2)
          .thenApply(num->num +5)
          .thenApply(num->String.valueOf(num))
          .thenAccept(txt -> System.out.println("resultado: " + txt));

  cadena.join();
 }

}
