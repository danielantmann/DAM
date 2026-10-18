package Threads.Ejercicios.practicaExam2;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

//Hay 10 coches que intentan aparcar en un parking que dispone de tres plazas.
//Requisitos:
//        1. Cada coche se ejecuta como una tarea concurrente.
//        2. Utiliza un ExecutorService y Semaphore(3).
//        3. Antes de aparcar, cada coche obtiene un permiso con acquire().
//        4. Simula una estancia de entre uno y dos segundos.
//5. Cada coche debe liberar el permiso una vez haya terminado.
//        6. Ningún momento puede haber más de tres coches utilizando plaza simultáneamente.
//Con parking.availablePermits() te quitas de en medio el AtomicInteger auxiliar, te ahorras variables de más,

public class Parking {

    static void main() {
        AtomicInteger plazasDisponibles = new AtomicInteger(3);
        Semaphore parking = new Semaphore(3);
        final int cantCoches  = 10;
        ExecutorService servicio  = Executors.newFixedThreadPool(cantCoches);
        for (int i = 1; i <= cantCoches ; i++) {
            int id = i;
            servicio.submit(()->{
                try {
                    parking.acquire();
                    int disponibles = plazasDisponibles.decrementAndGet();
                    System.out.println("Coche " + id + " entra. Plazas disponibles : " +disponibles);
                    Thread.sleep(2000);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }finally {
                    parking.release();
                     int liberadas  = plazasDisponibles.incrementAndGet();
                    System.out.println("Coche " + id + " sale. Plazas disponibles : " + liberadas);
                }
            });
        }
        servicio.shutdown();
    }
}
