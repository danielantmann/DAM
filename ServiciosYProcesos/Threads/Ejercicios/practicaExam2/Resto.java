package Threads.Ejercicios.practicaExam2;

//
//Ejercicio 2. Restaurante — 45 minutos (4 puntos)
//Desarrolla un programa que simule una cocina con pedidos.
//Condiciones del sistema:
//        - Un productor genera 12 pedidos identificados como Pedido-1, Pedido-2, etc.
//- Dos consumidores preparan los pedidos.
//- La cola tiene una capacidad máxima de cinco pedidos.
//- Cada preparación tarda entre 500 y 1.000 milisegundos.
//- Cada consumidor debe mostrar su nombre y el pedido que procesa.
//- Cada pedido debe contabilizarse una sola vez.
//Implementación obligatoria:
//Utiliza ArrayBlockingQueue<Pedido> o ArrayBlockingQueue<String> y un AtomicInteger para contar los pedidos completados.
//La parte más importante: el programa debe terminar correctamente cuando se hayan procesado todos los pedidos. No vale dejar a los consumidores bloqueados indefinidamente esperando nuevos pedidos.

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Resto {
    static void main() {
        final int cantidadPedidos = 12;
        final int cantidadConsumidores = 2;
        AtomicInteger pedidosGenerados = new AtomicInteger(0);
        AtomicInteger pedidosConsumidos = new AtomicInteger(0);
        ArrayBlockingQueue<String> cola = new ArrayBlockingQueue<>(5);
        ExecutorService productores = Executors.newFixedThreadPool(1);
        ExecutorService consumidores = Executors.newFixedThreadPool(2);

        productores.submit(()->{
            for (int i = 1; i <= cantidadPedidos ; i++) {
                try {
                    cola.put("Pedido " + i);
                    System.out.println("Pedido" + i);
                    pedidosGenerados.incrementAndGet();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        });

        for (int i = 1; i <= 2 ; i++) {
            int id = i;
        consumidores.submit(()->{
            for (int j = 0; j < 6; j++) {
                try {
                    String dato = cola.take();
                    pedidosConsumidos.incrementAndGet();
                    System.out.println("Consumidor " + id + dato);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        }

        productores.shutdown();
        consumidores.shutdown();

        // AÑADIR ESTO PARA QUE EL MAIN ESPERE A QUE TERMINE TODO
        try {
            // Esperamos hasta un tiempo prudencial (ej. 10 segundos) a que terminen
            productores.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS);
            consumidores.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("PedidosGenerados: " + pedidosGenerados.get());
        System.out.println("PedidosConsumidos: " + pedidosConsumidos.get());
        System.out.println("Consumidores : " + cantidadConsumidores);
    }
}

