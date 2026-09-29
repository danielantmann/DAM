package Threads.Ejercicios.EjerciociosClase.Ej5.Piscina;

import Threads.Ejercicios.EjerciociosClase.Ej5.ParkingSeparacionClases.Parking;

import java.util.Random;

public class Nadador implements Runnable{
    private final int id;
    private final Piscina piscina;
    private final Random random= new Random();

    public Nadador(int id, Piscina piscina) {
        this.id = id;
        this.piscina = piscina;
    }

    @Override
    public void run() {
        boolean haEntrado = false;
        try {
            piscina.entrar(id);
            haEntrado =true;
            Thread.sleep(500 + random.nextInt(1500));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            if (haEntrado){
                piscina.salir(id);
            }
        }
    }
}
