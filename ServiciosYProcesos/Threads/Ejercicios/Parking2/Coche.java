package Threads.Ejercicios.Parking2;

import java.util.concurrent.Semaphore;

public class Coche extends Thread{
    private final String nombre;
    private final Semaphore parking;

    public Coche(String nombre, Semaphore parking) {
        this.nombre = nombre;
        this.parking = parking;
    }

    public void run(){

        try {
            System.out.println("Coche " + nombre + " llego al parking");
            parking.acquire();
            System.out.println("Coche " + nombre + " aparcao");
            Thread.sleep(2000);
            System.out.println("Coche " + nombre + " saliendo del parking");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            parking.release();
        }
    }
}
