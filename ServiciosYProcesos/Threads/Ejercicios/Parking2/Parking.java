package Threads.Ejercicios.Parking2;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Parking {
    private final int PLAZAS = 4;
    private final int COCHES = 10;

    Semaphore plazasParking = new Semaphore(PLAZAS, true);

    public void iniciarDemo(){
        ArrayList<Coche> listaCoches = new ArrayList<>();

        for (int i = 1; i <= COCHES ; i++) {
            Coche coche = new Coche("Coche " + i ,plazasParking);
            listaCoches.add(coche);
            coche.start();
        }

        for (Coche coche : listaCoches){
            try {
                coche.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
