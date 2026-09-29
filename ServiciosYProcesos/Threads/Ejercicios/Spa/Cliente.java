package Threads.Ejercicios.Spa;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Cliente extends Thread {
    private final String nombre;
    private final Semaphore hidro;
    private final AtomicInteger contadorCliente;
    private final AtomicInteger pago;


    public Cliente(String nombre, Semaphore hidro, AtomicInteger contadorCliente, AtomicInteger pago) {
        this.nombre = nombre;
        this.hidro = hidro;
        this.contadorCliente = contadorCliente;
        this.pago = pago;
    }

    public void run (){
        System.out.println("cliente " + nombre  + " llego");
        try {
            hidro.acquire();
            System.out.println("cliente " + nombre  + " en hidro");
            Thread.sleep(2000);

            System.out.println("cliente " + nombre  + " sale");
            contadorCliente.incrementAndGet();
            pago.addAndGet((int) (Math.random() * (50 - 10 + 1)) + 10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            hidro.release();
        }

    }
}
