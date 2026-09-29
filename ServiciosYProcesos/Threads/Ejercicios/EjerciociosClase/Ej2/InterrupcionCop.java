package Threads.Ejercicios.EjerciociosClase.Ej2;
//
//Ejercicio 2.3 — Interrupción cooperativa
//Crea un hilo que ejecute un bucle infinito comprobando Thread.currentThread().isInterrupted().
// Desde el main, espera 2 segundos y llama a interrupt().
// El hilo debe terminar de forma "educada" mostrando un mensaje antes de morir.

public class InterrupcionCop {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(()->{
            while (!Thread.currentThread().isInterrupted()){

            }
            System.out.println("interrupcion de forma educada");
        });
      t.start();
      t.sleep(200);
      t.interrupt();
    }
}
