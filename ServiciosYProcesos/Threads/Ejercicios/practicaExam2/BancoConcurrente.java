package Threads.Ejercicios.practicaExam2;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BancoConcurrente {
    static Semaphore cajero = new Semaphore(3,true);
    static AtomicInteger totalAtendidos = new AtomicInteger(0);
    static AtomicInteger totalSacado = new AtomicInteger(0);
    static void main() {
        Random numAleatorio = new Random();
        ExecutorService clientes = Executors.newCachedThreadPool();
        for (int i = 1; i <= 10 ; i++) {
            int clienteId = i;
            clientes.submit(()->{
                try {
                    cajero.acquire();
                    int totalRetirado = numAleatorio.nextInt(49,501);
                    Thread.sleep(numAleatorio.nextInt(499,1001));
                    System.out.println("Cliente "+clienteId+ " ,retiro: " +totalRetirado);
                    totalAtendidos.incrementAndGet();
                    totalSacado.getAndAdd(totalRetirado);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }finally {
                    cajero.release();
                }

            });
        }

        clientes.shutdown();

        try {
            clientes.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Total clientes: " + totalAtendidos);
        System.out.println("Total retirado: " + totalSacado);
    }


}
