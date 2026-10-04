package Threads.Ejercicios.EjerciociosClase.Ej5;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockContadorRoto {
    private int contador = 0;
    private final ReentrantLock cerrojo = new ReentrantLock();

    public void incrementar(){
        cerrojo.lock();
        try{
            contador ++;
        }finally {
            cerrojo.unlock();
        }
    }

    public int getContador() {
        cerrojo.lock();
        try {
            return contador;
        } finally {
            cerrojo.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLockContadorRoto contadorRoto = new ReentrantLockContadorRoto();

        Runnable tarea = () -> {
            for (int i = 0; i < 10000; i++) {

                contadorRoto.incrementar();
            }
        };

        Thread[] hilos = new Thread[10];
        for (int i = 0; i < 10; i++) {
            hilos[i] = new Thread(tarea);
        }

        for (Thread t : hilos) t.start();
        for (Thread t : hilos) t.join();

        System.out.println("Resultado (esperado 100000): " + contadorRoto.getContador());
    }
}
