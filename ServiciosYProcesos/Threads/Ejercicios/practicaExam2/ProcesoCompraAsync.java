package Threads.Ejercicios.practicaExam2;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Proceso de compra — 45 minutos (4,5 puntos)
//Una tienda online debe comprobar tres condiciones independientes antes de confirmar una compra:
//Requisitos obligatorios
//A. Ejecución asíncrona
//Las tres operaciones deben poder empezar sin que una tenga que esperar a que termine la anterior. Utiliza supplyAsync() y un ExecutorService propio.
//B. Decisión de compra
//Cuando las tres operaciones terminen, muestra "Compra confirmada" únicamente cuando:
//        - El stock esté disponible.
//        - El precio sea mayor que cero.
//        - El envío esté disponible.
//Si alguna condición no se cumple, muestra "Compra no disponible".
//C. Coordinación
//Utiliza thenCombine() o CompletableFuture.allOf() para coordinar las tareas independientes.
//D. Manejo de errores
//Si alguna operación falla con una excepción, el programa debe mostrar "Compra no disponible" y no terminar con una excepción sin controlar.
//E. Timeout
//Establece un tiempo máximo de cinco segundos para la operación global con orTimeout() o una estrategia equivalente basada en los métodos estudiados.
//F. Cierre
//Cierra el executor correctamente y muestra el resultado final.
//Prueba al menos estas cuatro situaciones:
//Stock	Precio	Envío	Resultado
//Disponible	25 €	Disponible	Compra confirmada
//No disponible	25 €	Disponible	Compra no disponible
//Disponible	0 €	Disponible	Compra no disponible
//Disponible	25 €	No disponible	Compra no disponible
//Como prueba adicional, haz que una consulta lance una excepción y que otra tarde más del timeout.
public class ProcesoCompraAsync {
    static void main() {
        final boolean stock = true;
        final double precio = 10.00;
        final boolean envio = true;
        Random aleatorio  = new Random();
        ExecutorService servicio = Executors.newFixedThreadPool(3);
        CompletableFuture<Boolean> comprobarStock = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(aleatorio.nextInt(100,400));
                return stock;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },servicio) ;

        CompletableFuture<Double> comprobarPrecio = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(aleatorio.nextInt(100,400));
                return precio;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },servicio) ;

        CompletableFuture<Boolean> comprobarEnvio = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(aleatorio.nextInt(100,400));
                return envio;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },servicio) ;

        //All. OFF

        CompletableFuture<Void> todasLasTareas = CompletableFuture.allOf(comprobarStock,comprobarPrecio,comprobarEnvio);

        CompletableFuture<String> resultadoFinal = todasLasTareas.thenApply(v->{
           try {
               boolean stockOk = comprobarStock.join();
               boolean envioOk = comprobarEnvio.join();
               double precioOk = comprobarPrecio.join();

               if (stockOk && envioOk && precioOk > 0){
                   return "compra confirmada ";
               }else {
                   return "compra no disponible";
               }
           } catch (Exception e){
               return "compra no disponible";
           }

        })
                // Requisito E: Timeout global de 5 segundos
                .orTimeout(5, java.util.concurrent.TimeUnit.SECONDS)

                // Requisito D: Manejo de errores
                .exceptionally(ex -> "Compra no disponible");

        resultadoFinal.thenAccept(System.out::println).join();
        servicio.shutdown();
//then combine
//        CompletableFuture<String> resultadoFinal = comprobarStock
//                .thenCombine(comprobarPrecio, (tieneStock, valorPrecio) -> {
//                    // Aquí tenemos el stock y el precio
//                    return tieneStock && (valorPrecio > 0);
//                })
//                .thenCombine(comprobarEnvio, (condicionAnterior, tieneEnvio) -> {
//                    // Combinamos con el envío. condicionAnterior tiene el (stock && precio>0)
//                    boolean compraValida = condicionAnterior && tieneEnvio;
//
//                    if (compraValida) {
//                        return "Compra confirmada";
//                    } else {
//                        return "Compra no disponible";
//                    }
//                })
//                // Requisito E: Timeout de 5 segundos
//                .orTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
//
//                // Requisito D: Manejo de errores (si algo falla o salta el timeout)
//                .exceptionally(ex -> "Compra no disponible");
//
//        // 2. Mostramos el resultado final y cerramos el executor (Requisito F)
//        resultadoFinal.thenAccept(mensaje -> {
//            System.out.println("Resultado: " + mensaje);
//        }).join(); // Esperamos a que todo termine
//
//        // 3. Cerramos el pool de hilos
//        servicio.shutdown();

    }
}
