package Threads.Ejercicios.practicando.Ej02_CicloLife;

import java.util.Random;

public class Descargas_join extends Thread{
    final String nombre;

    public Descargas_join(String nombre) {
        this.nombre = nombre;
    }

    public void run(){
        System.out.println("Descargando archivo: " + nombre);
        try {
            Thread.sleep((int)(Math.random() * 3000) + 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(nombre + "ha finalizado");
    }

    static void main() {
        Random numAleatorio = new Random();

        Thread[] listaHilos = new Thread[3];
        for (int i = 1; i <= 3; i++) {
            Descargas_join hilo = new Descargas_join("Archivo " +i);
            listaHilos[i -1] = hilo;
            hilo.start();

        }

        for (Thread hilo : listaHilos){
            try {
                hilo.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Terminaron todas las descargas");
    }

}
