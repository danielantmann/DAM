package Threads.Ejercicios.Parking;

import java.util.concurrent.Semaphore;

public class MainParking {
    public static void main(String[] args) {

        Semaphore parkingSemaphore = new Semaphore(50, true);

        for (int i = 0; i < 80 ; i++) {
            Car car = new Car("Car " + i , parkingSemaphore);
            car.start();
        }
    }
}
