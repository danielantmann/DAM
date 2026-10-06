package Threads.Ejercicios.PracticaExamen;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Alamacen {
    int stock = 50;
    ReentrantLock candado = new ReentrantLock();

    public void retirarCantidad(int cantidad){
        this.candado.lock();

        try {
            this.stock = this.stock - cantidad;
            System.out.println("Stock: " + this.stock);
        }finally {
            candado.unlock();
        }

    }

    static void main() {
        ExecutorService trabajadores = Executors.newFixedThreadPool(3);
        Alamacen alamacen = new Alamacen();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            trabajadores.submit(()->{
                alamacen.retirarCantidad(random.nextInt(1,20));
            });
        }
        trabajadores.shutdown();
    }
}
