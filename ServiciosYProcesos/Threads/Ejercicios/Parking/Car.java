package Threads.Ejercicios.Parking;

import java.util.concurrent.Semaphore;

public class Car  extends  Thread{
    private final String name;
    private final Semaphore parking;

    public Car(String name, Semaphore parking){
        this.name = name;
        this.parking = parking;
    }

    @Override
    public void run(){

        try {
            System.out.println("Coche " + name + " llego al prking");
            parking.acquire();
            System.out.println("Coche " + name + " APARCAO");
            Thread.sleep(2000);
            System.out.println("Coche " + name + " saliendo del parking");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            parking.release();
        }
    }
}
