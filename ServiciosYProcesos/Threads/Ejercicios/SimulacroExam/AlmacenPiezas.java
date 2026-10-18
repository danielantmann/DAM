package Threads.Ejercicios.SimulacroExam;

//
//El almacén de piezas
//Enunciado:
//Implementa una aplicación de cadena de montaje:
//
//Un hilo productor genera 20 piezas secuencialmente (con un identificador único como "Pieza 1", "Pieza 2"...).
//
//Hay dos robots (consumidores) que procesan las piezas de la cinta.
//
//La cinta transportadora es una cola (BlockingQueue<String>) que admite como máximo 4 piezas.
//
//Procesar cada pieza tarda entre 200 y 600 milisegundos.
//
//Cada pieza debe procesarse una sola vez.
//
//Requisitos técnicos: Utiliza ArrayBlockingQueue, ExecutorService y AtomicInteger para llevar el control. La aplicación debe finalizar de manera ordenada sin quedarse bloqueada en el take().

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AlmacenPiezas {
    static void main() {
        final int cantidadPiezas = 20;
        AtomicInteger piezasProducidas = new AtomicInteger(0);
        Random aleatorio  = new Random();
        ArrayBlockingQueue<String> cola = new ArrayBlockingQueue<>(4);
        ExecutorService productor = Executors.newFixedThreadPool(1);
        ExecutorService consumidor = Executors.newFixedThreadPool(2);

        for (int i = 1; i <= cantidadPiezas ; i++) {
            int id = i;
            productor.submit(()->{
                try {
                    cola.put("Pieza " +id);
                    Thread.sleep(aleatorio.nextInt(200,600));
                    System.out.println("Productor temina la pieza" + id);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        for (int i = 1; i <=2 ; i++) {
            int id = i;
            consumidor.submit(()->{
                while (piezasProducidas.get() < cantidadPiezas){
                    try {
                        String dato  = cola.poll(200, TimeUnit.MILLISECONDS);

                        if (dato != null){
                            piezasProducidas.incrementAndGet();
                            Thread.sleep(aleatorio.nextInt(200,600));
                            System.out.println("Robot " + id + "  " + dato );
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        }

        productor.shutdown();
        consumidor.shutdown();

        try {
            productor.awaitTermination(10, TimeUnit.SECONDS);
            consumidor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
