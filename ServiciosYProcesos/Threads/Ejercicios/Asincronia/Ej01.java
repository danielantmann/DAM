package Threads.Ejercicios.Asincronia;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Ej01 {
    static void main() throws ExecutionException, InterruptedException {
        ExecutorService ejecutor = Executors.newFixedThreadPool(3);
        Future <Integer> future = ejecutor.submit(()->{
            try {
                Thread.sleep(2000);
                return 42;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Tarea lanzada");
        Integer resultado = null;
        try {
             resultado = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("resultado:" + resultado);
        ejecutor.shutdown();
    }
}
