package Threads.Ejercicios.practicando.Ej05Reentran;

import java.util.concurrent.Semaphore;

public class SemaphorePaking {
        Semaphore parking;

    public SemaphorePaking(int plazas, boolean fair) {
        this.parking = new Semaphore(plazas, fair);
    }


    static void main() {
    SemaphorePaking parking = new SemaphorePaking(3,true);

        for (int i = 1; i <= 10; i++) {
            int id = i;
            Thread coche = new Thread(()->{
                try {
                    parking.parking.acquire();
                    Thread.sleep(3000);
                    System.out.println("coche " + id + "ha aparcado, plazas libres: " +parking.parking.availablePermits());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }finally {
                    parking.parking.release();
                    System.out.println("Plazas libres: " + parking.parking.availablePermits());

                }
            });
        coche.start();
        }
    }


}
