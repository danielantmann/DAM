package Threads.Ejercicios.EjerciociosClase.Ej6;


//Ejercicio 6.1 — Productor-consumidor con BlockingQueue
//Reescribe el ejercicio 4.2 (productor-consumidor con N productores/consumidores)
//usando ArrayBlockingQueue en lugar de wait()/notify() manuales. Compara la cantidad de código necesario frente a la solución manual.

import java.util.concurrent.ArrayBlockingQueue;

public class BlockingQueue {
    public static void main(String[] args) {
        ArrayBlockingQueue<Integer> cola = new ArrayBlockingQueue<>(3);

        Thread productor = new Thread(()->{
            for (int i = 0; i < 5; i++) {
                System.out.println("Fabricando: " + i);
                try {
                    cola.put(i);
                    System.out.println("Metido en la estanteria: " + i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread consumidor = new Thread(()->{
            for (int i = 0; i < 5; i++) {
                try {
                    Integer valor = cola.take();
                    System.out.println("Sacando: " + valor);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    productor.start();
    consumidor.start();
    }
}
