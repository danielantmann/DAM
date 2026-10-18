package Threads.Ejercicios.practicaExam2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RepartidosAsincrono {


    static void main() {
       Random numeroAleatorio = new Random();
        List<Future<Integer>> paquetes = new ArrayList<>();
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 1; i <=10 ; i++) {
            Future<Integer> resultado = executor.submit(()->{
                try {
                    Thread.sleep(500);
                    int totalPaquete = numeroAleatorio.nextInt(9,51);
                    return totalPaquete;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            paquetes.add(resultado);
        }
        executor.shutdown();

        for (Future<?> p : paquetes){
            try {
                System.out.println("total envio del paquete: " + p.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
