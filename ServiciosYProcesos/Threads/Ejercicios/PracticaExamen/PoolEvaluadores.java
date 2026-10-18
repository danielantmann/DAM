package Threads.Ejercicios.PracticaExamen;

//
//Ejercicio 1: El Pool de Evaluadores (ExecutorService + Callable)
//Enunciado:
//Crea un programa que lance un pool de hilos fijo de 3 trabajadores usando
// Executors.newFixedThreadPool(3). Cada trabajador será un Callable<String> que
// simula corregir un examen tardando 500 ms (Thread.sleep) y devuelve un mensaje c
// omo "Examen del alumno X corregido con éxito".
//
//Requisito: Lanza 5 tareas, almacena todos los objetos Future<String> devueltos en
// una lista, recorre la lista imprimiendo el resultado con .get(), y no olvides apagar correctamente el executor con shutdown().


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PoolEvaluadores implements Callable<String> {
    private int id;


    public PoolEvaluadores(int id) {
        this.id = id;
    }

    @Override
    public String call()  {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return "El examen del alumno: " +this.id + " , corregido";
    }

    static void main() {
        List<Future<String>> listaFuturo = new ArrayList<>();
        ExecutorService servicio = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 5 ; i++) {

            Callable<String> evaluadores = new PoolEvaluadores(i);
            listaFuturo.add(servicio.submit(evaluadores));
        }

        servicio.shutdown();
        for(Future<String> futuro : listaFuturo){
            try {
                System.out.println(futuro.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
