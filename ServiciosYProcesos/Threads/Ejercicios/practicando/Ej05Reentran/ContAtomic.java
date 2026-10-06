package Threads.Ejercicios.practicando.Ej05Reentran;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ContAtomic {

    AtomicInteger contador = new AtomicInteger(0);

    public void incrementar(){
        contador.incrementAndGet();
    }

    public int getContador() {
        return contador.get();
    }

    public static void main(String[] args) throws InterruptedException {
        ContAtomic c = new ContAtomic();
        Runnable tarea = () -> {
            for (int i = 0; i < 10_000; i++) c.incrementar();
        };

        Thread[] hilos = new Thread[10];
        for (int i = 0; i < 10; i++) hilos[i] = new Thread(tarea);
        for (Thread t : hilos) t.start();
        for (Thread t : hilos) t.join();

        System.out.println("Resultado (esperado 100000): " + c.getContador());
    }
}
