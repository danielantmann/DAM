package Threads.Ejercicios.EjerciociosClase.Ej5;

import java.util.concurrent.atomic.AtomicInteger;

//
//Ejercicio 5.2 — Contador con AtomicInteger
//Reimplementa de nuevo el mismo contador, esta vez usando AtomicInteger y su método incrementAndGet(). Compara el código con las dos versiones anteriores: ¿cuál es más simple?
public class AtomicIntegerContador {
    private final AtomicInteger contador = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger contador = new AtomicInteger();

        Runnable tarea = ()->{
            for (int i = 0; i < 10000; i++) {
                contador.incrementAndGet();
            }
        };

        Thread[] hilos = new Thread[10];

        for (int i = 0; i < 10; i++) {
            hilos[i] = new Thread(tarea);
        }

        for (Thread t : hilos) t.start();
        for (Thread t : hilos) t.join();

        System.out.println("Resultado (esperado 100000): " + contador.get());
    }
}
