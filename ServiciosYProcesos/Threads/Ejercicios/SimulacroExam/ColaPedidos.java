package Threads.Ejercicios.SimulacroExam;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//
//Cola de pedidos — 45 minutos (4,5 puntos)
//Implementa una aplicación de restaurante.
//- Un productor genera 15 pedidos.
//- Cada pedido debe tener un identificador único.
//- Hay tres cocineros que procesan los pedidos.
//- La cola admite como máximo cinco pedidos.
//- Preparar cada pedido tarda entre 500 y 1.500 milisegundos.
//- Los pedidos se pueden procesar en cualquier orden de finalización, pero cada uno debe procesarse una sola vez.
//Debes utilizar:
//BlockingQueue<String>
//
//
//con una implementación de capacidad limitada, ExecutorService y AtomicInteger.
//La aplicación debe finalizar de manera ordenada después de procesar los 15 pedidos. No debe quedarse esperando indefinidamente en take().
public class ColaPedidos {
    static void main() {
        final int cantidadPedidos  = 15;
        AtomicInteger pedidosGenerados = new AtomicInteger(0);
        AtomicInteger pedidosCompletados = new AtomicInteger(0);
        AtomicInteger pedidosPendientes = new AtomicInteger(cantidadPedidos);
        ExecutorService productores = Executors.newFixedThreadPool(1);
        ExecutorService consumidores = Executors.newFixedThreadPool(3);
        ArrayBlockingQueue<String> cola = new ArrayBlockingQueue<>(5);
        Random numeroAleatorio = new Random();

        for (int i = 1; i <= cantidadPedidos ; i++) {
            int idPedido = i;
            productores.submit(()->{
                try {
                    cola.put("Pedido " +idPedido);
                    pedidosGenerados.incrementAndGet();
                    Thread.sleep(numeroAleatorio.nextInt(500,1500));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }


        //CON WHILE DINAMICO

        // 3 cocineros procesando de forma concurrente
        for (int i = 1; i <= 3; i++) {
            int id = i;
            consumidores.submit(() -> {
                // Mientras no hayamos completado los 15 pedidos en total entre todos
                while (pedidosCompletados.get() < cantidadPedidos) {
                    try {
                        // poll() o take() con control. Usamos take() pero vigilando.
                        // O mejor aún, si la cola está vacía un momento, sacamos con control:
                        String dato = cola.poll(200, TimeUnit.MILLISECONDS);
                        if (dato != null) {
                            pedidosCompletados.incrementAndGet();
                            pedidosPendientes.decrementAndGet();

                            // Simular tiempo de preparación (entre 500 y 1500 ms)
                            Thread.sleep(numeroAleatorio.nextInt(500, 1500));

                            System.out.println("Cocinero " + id + " procesó: " + dato);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        //CON FOR RIGIDO
//        for (int i = 0; i < 3; i++) {
//            int id = i;
//            consumidores.submit(()->{
//                for (int j = 0; j < 5 ; j++) {
//
//                    try {
//                        String dato = cola.take();
//                        pedidosCompletados.incrementAndGet();
//                        pedidosPendientes.decrementAndGet();
//                        System.out.println("cocinero " +id + "  " +dato);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//
//            });
//
//        }

        productores.shutdown();
        consumidores.shutdown();

        try {
            productores.awaitTermination(10, TimeUnit.SECONDS);
            consumidores.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("PedidosGenerados: " + pedidosGenerados.get());
        System.out.println("PedidosConsumidos: " + pedidosCompletados.get());
        System.out.println("Pedidos pendientes: " + pedidosPendientes.get());
    }
}
