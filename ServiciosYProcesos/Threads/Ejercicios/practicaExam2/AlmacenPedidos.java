package Threads.Ejercicios.practicaExam2;

import java.util.concurrent.ArrayBlockingQueue;

public class AlmacenPedidos {
    static void main() {
        ArrayBlockingQueue<String> cinta = new ArrayBlockingQueue<>(5);

        Thread productor = new Thread(()->{
            for (int i = 0; i < 15; i++) {
                try {
                    String item = "Pedido #" + i;
                    cinta.put(item);
                    System.out.println("➡️ Producido: " + item);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        });
        productor.start();
        Thread consumidor = new Thread(()->{
            for (int i = 0; i < 15; i++) {
                try {
                    String item = cinta.take();
                    System.out.println("⬅️ Consumido: " + item);
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        });
        consumidor.start();

        try {
            productor.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            consumidor.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
