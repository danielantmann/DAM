package Threads.Ejercicios.practicaExam2;


//Ejercicio 3. Tareas y resultados — 30 minutos (2,5 puntos)
//Crea un ExecutorService con tres hilos.
//Debes enviar cinco tareas mediante submit().
// Cada tarea recibe un número del 1 al 5,
// espera un tiempo diferente y devuelve su cuadrado.

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class TareasResultados {
    static void main() {
        ExecutorService servicio  = Executors.newFixedThreadPool(3);
        List<Future<Integer>> listaResultados = new ArrayList<>();
        Random numAleatorio = new Random();

        for (int i = 1; i <= 5 ; i++) {
            int numero = i;
            Future<Integer> resultado = servicio.submit(()->{
                Thread.sleep(numAleatorio.nextInt(200,500));
                return numero * numero;


            });

            listaResultados.add(resultado);
        }

    int resultado = 0;
    int tarea = 1;
        for (Future<Integer> num : listaResultados){
            try {
                System.out.println("Tarea " + tarea + " devuelve: "  + num.get());
                resultado =  resultado + num.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            tarea ++;
        }

        System.out.println("Resultado: "  +resultado);
        servicio.shutdown();
    }
}
