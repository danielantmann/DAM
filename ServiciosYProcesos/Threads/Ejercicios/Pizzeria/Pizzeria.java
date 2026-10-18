package Threads.Ejercicios.Pizzeria;

import Threads.Ejercicios.EjerciociosClase.Ej6.BlockingQueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Pizzeria {
   private final ArrayBlockingQueue<Pedido> cola = new ArrayBlockingQueue<>(5);
   private final Semaphore hornos = new Semaphore(2);
   private final AtomicInteger pedidosCompletados = new AtomicInteger(0);
   private final CountDownLatch conteo = new CountDownLatch(20);

    public ArrayBlockingQueue<Pedido> getCola() {
        return cola;
    }

    public Semaphore getHornos() {
        return hornos;
    }

    public AtomicInteger getPedidosCompletados() {
        return pedidosCompletados;
    }

    public CountDownLatch getConteo() {
        return conteo;
    }
}
