package Threads.Ejercicios.EjerciociosClase.Ej5.ParkingSeparacionClases;

import java.util.Random;

public class Coche implements Runnable{
    private final int id;
    private final Parking parking;
    private final Random random = new Random();


    public Coche(int id, Parking parking) {
        this.id = id;
        this.parking = parking;
    }
    @Override
    public void run(){
        try {
            parking.entrar(id);
            Thread.sleep(500 + random.nextInt(1500));
            parking.salir(id);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
