package Threads.Ejercicios.EjerciociosClase.Ej1;

import java.time.LocalTime;

//jercicio 1.4 — Hilos daemon
//Crea un hilo daemon que imprima la hora actual cada segundo indefinidamente.
// Comprueba que el programa termina en cuanto acaba el main, aunque el hilo daemon no haya sido detenido explícitamente.

public class HilosDaemon {
    public static void main(String[] args) throws InterruptedException {
        Thread clock = new Thread(() ->{
            System.out.println("Hora: " + LocalTime.now());

        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;

            }
        }
        }) ;

        clock.setDaemon(true);
        clock.start();

        Thread.sleep(3500);
        System.out.println("Fin del main, el daemon morirá con él.");
    }
}
