package Threads.Ejercicios.practicando.Ej07PoolHilos;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PoolFijo {
    static void main() {
        ExecutorService servicio  = Executors.newFixedThreadPool(4);
        Random num = new Random();
        for (int i = 0; i < 20; i++) {
            int id= i;
            servicio.submit(()->{
                System.out.println("Procesando pedido id: " +id);
                try {
                    Thread.sleep(num.nextInt(1000,3000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        servicio.shutdown();
    }
}
