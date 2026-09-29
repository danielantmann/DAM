package Threads.Ejercicios.EjerciociosClase.Ej5.ParkingSeparacionClases;

import java.util.Random;

public class MainAparcamiento {
    public static void main(String[] args) {
        Parking parking = new Parking(3);
        Random random = new Random();

        for (int i = 1; i <= 10 ; i++) {
            Thread coche = new Thread(new Coche(i, parking));
            coche.start();
            try {
                Thread.sleep(random.nextInt(300));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
