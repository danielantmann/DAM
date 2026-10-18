package Threads.Ejercicios.practicaExam2;

//
//Sistema de Precios Online — 30-40 minutos (2,5 puntos)
//Desarrolla un programa en Java utilizando CompletableFuture que simule el cálculo del precio final de un producto aplicando un descuento de forma asíncrona.
//
//Requisitos técnicos:
//Tarea 1 (obtenerPrecioBase):
//
//Debe ejecutarse de forma asíncrona (supplyAsync).
//
//Simula una consulta a una base de datos con un retraso de 400 milisegundos.
//
//Debe devolver un precio base de tipo Double (por ejemplo, 150.0).
//
//Tarea 2 (obtenerDescuento):
//
//Debe ejecutarse de forma asíncrona en paralelo (supplyAsync).
//
//Simula una consulta a un servicio de cupones con un retraso de 250 milisegundos.
//
//Debe devolver un porcentaje de descuento de tipo Double (por ejemplo, 0.20 que representa un 20%).
//
//Combinación (thenCombine):
//
//Combina el resultado del precio base y del descuento cuando ambos terminen.
//
//Calcula el precio final aplicando la fórmula: precioBase * (1 - descuento).
//
//Salida y Sincronización:
//
//Muestra por pantalla el precio base, el descuento y el precio final calculado.
//
//        Utiliza join() (o get()) en el hilo principal para esperar a que todo el proceso termine antes de que el programa finalice.

import java.util.concurrent.CompletableFuture;

public class CalcularPrecioAsync {
    static void main() {
        CompletableFuture<Double> precio  = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(500);
                return 150.00;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Double> descuento  = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(500);
                return 0.2;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Void> resultado = precio.thenCombine(descuento,(res, desc) ->{
           double resDesc = res *( 1-desc);
                    return "Precio base: " + res + "€\n" +
                            "Descuento: " + (desc * 100) + "%\n" +
                            "Precio final: " + resDesc + "€";
        })
                .thenAccept(txt-> System.out.println(txt));
    resultado.join();
    }

}
