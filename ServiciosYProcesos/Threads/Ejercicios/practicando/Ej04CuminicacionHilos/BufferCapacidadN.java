package Threads.Ejercicios.practicando.Ej04CuminicacionHilos;

import java.util.LinkedList;
import java.util.Queue;

public class BufferCapacidadN extends Thread{
    private final Queue<Integer> cola = new LinkedList<>();
    private final int capacidad;

    public BufferCapacidadN(int capacidad) {
        this.capacidad = capacidad;
    }

    public synchronized void producir(int valor){
        while(cola.size() >=  capacidad){
            try {
                wait();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Producido: " + valor);
        cola.add(valor);
        notifyAll();
    }

    public synchronized int consumir(){
        while(cola.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        int valor = cola.poll();
        System.out.println("Consumido: " + valor);
        notifyAll();
        return valor;
    }

    static void main() {
    BufferCapacidadN buffer = new BufferCapacidadN(5);

        for (int i = 1; i <= 3; i++) {
            int idProd = i;
            Thread productor  = new Thread(()->{
                for (int j = 0; j < 10; j++) {
                    buffer.producir(idProd);
                }
            });
            productor.start();
        }


        for (int i = 1; i <= 3; i++) {
            Thread consumidor = new Thread(()->{
                for (int j = 0; j <10 ; j++) {
                    buffer.consumir();
                }

            });
            consumidor.start();
        }
    }
}
