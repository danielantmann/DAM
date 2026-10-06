package Threads.Ejercicios.practicando.Ej06BlockinQueue;

import java.util.concurrent.ArrayBlockingQueue;

public class BufferBlockingQueue {

    private final ArrayBlockingQueue<Integer> cola;

    public BufferBlockingQueue(int capacidad) {
        this.cola = new ArrayBlockingQueue<>(capacidad);
    }

    public void producir(int valor){
        try {
            cola.put(valor);
            System.out.println("Producido: " + valor);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void consumir(){
        try {
            int valor =  cola.take();
            System.out.println("consumido: " + valor);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static void main() {
        BufferBlockingQueue buffer = new BufferBlockingQueue(5);

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

