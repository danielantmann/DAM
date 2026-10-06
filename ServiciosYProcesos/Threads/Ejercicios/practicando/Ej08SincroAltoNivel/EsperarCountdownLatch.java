package Threads.Ejercicios.practicando.Ej08SincroAltoNivel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EsperarCountdownLatch {
    static void main() {
        CountDownLatch tareas = new CountDownLatch(5);
        ExecutorService servicio = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            servicio.submit(()->{
                System.out.println("Empezando tarea");
                tareas.countDown();
                System.out.println("terminando tarea");
            });
        }
        try {
            tareas.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        servicio.shutdown();
    }
}
