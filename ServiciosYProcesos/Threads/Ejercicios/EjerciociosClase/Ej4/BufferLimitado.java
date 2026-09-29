package Threads.Ejercicios.EjerciociosClase.Ej4;

import java.util.LinkedList;
import java.util.Queue;

//El Reto: BufferLimitado (Capacidad 3)
//Imagina una estantería que como máximo puede contener 3 productos a la vez.
//
//El Productor debe fabricar y añadir elementos a una lista o cola. Si la estantería llega
// a 3 elementos (llena), tiene que esperar (wait()).
//
//El Consumidor debe retirar elementos. Si la estantería se queda a 0 elementos (vacía),
// tiene que esperar (wait()).
//
//Usa notifyAll() cada vez que produzcas o consumas para despertar al otro hilo.

public class BufferLimitado {
 private final Queue<Integer> cola= new LinkedList<>();
 private final int capacidad = 3;

 public synchronized void producir(int v) throws InterruptedException {
     while ( cola.size() >= capacidad){
         wait();
     }
     cola.add(v);
     System.out.println("produciendo" + v);
     notifyAll();
 }

 public synchronized int consumir() throws InterruptedException {
   while (cola.isEmpty()){
       wait();
   }
   int v = cola.poll();
     System.out.println("consumido" + v);
     notifyAll();
     return v;
 };

    public static void main(String[] args) {
        BufferLimitado buffer = new BufferLimitado();
        Thread productor = new Thread(()->{
            for (int i = 1; i <= 5 ; i++) {
                try {
                    buffer.producir(i);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread consumir = new Thread(()->{
            for (int i = 1; i <= 5 ; i++) {
                try {
                    buffer.consumir();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        productor.start();
        consumir.start();
    }
}
