package Threads.Ejercicios.PracticaExamen;

import java.util.concurrent.Semaphore;

public class ControlAcceso {
    private Semaphore semaforo = new Semaphore(2,true);

    public void entrar(){
        try {
            this.semaforo.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void salir(){
        this.semaforo.release();
    }

    static void main() {
        ControlAcceso control = new ControlAcceso();

        Thread[] listaHilos = new Thread[5];
        for (int i = 0; i < 5 ; i++) {
            Thread hilo = new Thread(()->{

            try {
                control.entrar();
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            control.salir();

            });
            hilo.start();
        }
    }
}
