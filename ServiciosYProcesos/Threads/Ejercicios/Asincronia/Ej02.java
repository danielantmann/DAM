package Threads.Ejercicios.Asincronia;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Ej02 {
    static void main() throws ExecutionException, InterruptedException {
        ExecutorService ejecutor = Executors.newFixedThreadPool(3);
        int resultado = 0;
        Future<Integer> numTarea1 = ejecutor.submit(()->{
            try {
                Thread.sleep(200);

                return 10;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            resultado += numTarea1.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        Future<Integer> numTarea2 = ejecutor.submit(()->{
            try {
                Thread.sleep(100);

                return 20;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            resultado += numTarea1.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        Future<Integer> numTarea3 = ejecutor.submit(()->{
            try {
                Thread.sleep(300);

                return 30;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            resultado = numTarea1.get() + numTarea2.get() + numTarea3.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        System.out.println("resultado :" +resultado);
    }
}
