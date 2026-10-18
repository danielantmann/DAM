package Threads.Ejercicios.practicaExam2;
//El Sistema de Sensores y Procesadores
//Imagina que tienes una arquitectura de streaming de datos:
//
//El buffer compartido: Una ArrayBlockingQueue<String> con una capacidad máxima de 5 huecos.
//
//        Los Productores (Sensores): Hay 3 hilos productores. Cada uno de ellos va a generar y meter en la cola 5 mensajes (por ejemplo: "Sensor A - Dato 1", etc.) usando .put().
//
//Los Consumidores (Procesadores): Hay 2 hilos consumidores que van sacando los mensajes de la cola con .take() y simulan procesarlos con un Thread.sleep(200).
//
//El toque de examen con CountDownLatch:
//
//Queremos usar un CountDownLatch para que el hilo principal (o un hilo supervisor) sepa exactamente cuándo los 3 productores han terminado de enviar todos sus datos.
//
//Cuando los 3 productores terminan, el hilo principal imprime "¡Todos los sensores han terminado de transmitir!" (mientras los consumidores pueden seguir vaciando la cola).

import java.util.concurrent.*;

public class Sensores {
    static void main() {
        ArrayBlockingQueue<String> buffer = new ArrayBlockingQueue<>(5);
        ExecutorService productores = Executors.newFixedThreadPool(3);
        ExecutorService consumidores = Executors.newFixedThreadPool(2);
        CountDownLatch tareas = new CountDownLatch(3);

        for (int i = 1; i <=3  ; i++) {
            String sensor = String.valueOf(i);
            productores.submit(()->{
                for (int j = 1; j <=5 ; j++) {
                    String dato = "Sensor " + sensor + " Dato: " + j;
                    try {
                        buffer.put(dato);
                        System.out.println(dato);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
                tareas.countDown();
            });

        }

        for (int i = 0; i < 15; i++) {
            consumidores.submit(()->{
                try {
                    String datoConsumido= buffer.take();
                    Thread.sleep(500);
                    System.out.println("Consumido el dato" + datoConsumido);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });
        }

        try {
            tareas.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Sensores terminaron de transmitir");
        consumidores.shutdown();
        productores.shutdown();
    }

}
