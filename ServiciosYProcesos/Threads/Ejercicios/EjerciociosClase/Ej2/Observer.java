package Threads.Ejercicios.EjerciociosClase.Ej2;
//Ejercicio 2.1 — Observador de estados
//Crea un hilo que duerma 3 segundos y, desde el main, imprime su estado (getState()) cada 500 ms mientras el hilo está vivo.
// Debes ver como mínimo los estados RUNNABLE/TIMED_WAITING y TERMINATED.

public class Observer {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(()->{
            try {
                Thread.sleep(300);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("Estado antes del start: " + t.getState());

        t.start();

        while (t.isAlive()){
            System.out.println("Estado: " + t.getState());
            Thread.sleep(500);
        }
        System.out.println("Estado final: " + t.getState());
    }
}
