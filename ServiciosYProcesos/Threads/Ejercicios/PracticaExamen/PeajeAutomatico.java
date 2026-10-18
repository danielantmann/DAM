package Threads.Ejercicios.PracticaExamen;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class PeajeAutomatico {
    static final int NUMERO_COCHES = 10;
    static  final int NUMERO_CABINAS = 2;

    static  Semaphore semaphore = new Semaphore(NUMERO_CABINAS,true);
    static Thread[] coches = new Thread[NUMERO_COCHES];
    static void main() {

        AtomicInteger cochesProcesados = new AtomicInteger(0);

        for (int i = 0; i < NUMERO_COCHES; i++) {
            int idCoche = i;

            coches[i] = new Thread(()->{
                System.out.println("Coche:" +idCoche+ "llega al peaje");

                try {
                    semaphore.acquire();
                    System.out.println("Coche:" +idCoche+ "pagando");
                    Thread.sleep(300);
                    System.out.println("Coche:" +idCoche+ "saliendo");
                    cochesProcesados.incrementAndGet();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }finally {
                    semaphore.release();
                }

            });
            coches[i].start();

        }

        for (Thread coche : coches){
            try {
                coche.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Final, cantidad de coches procesados: " + cochesProcesados.get());
    }

}
