package Threads.Ejercicios.practicando.Ej05Reentran;

import Threads.Ejercicios.EjerciociosClase.Ej3.ContadorRoto;

import java.util.concurrent.locks.ReentrantLock;

public class Reentranlock {
    private int contador = 0;
    ReentrantLock candado = new ReentrantLock();

    public void incrementar(){
        candado.lock();
        try {
            contador ++;
        }finally {
            candado.unlock();
        }
    }

    public int getContador() {
        return contador;
    }

    public static void main(String[] args) throws InterruptedException {
        Reentranlock c = new Reentranlock();
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

