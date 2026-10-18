package Threads.Ejercicios.practicaExam2;

//Validación y Envío de Pedido — 30-40 minutos (2,5 puntos)
//Desarrolla un programa en Java utilizando CompletableFuture que simule el flujo de procesamiento de un pedido online con las siguientes fases:
//
//Requisitos técnicos:
//Paso 1 (verificarStock):
//
//Debe ejecutarse de forma asíncrona (supplyAsync).
//
//Simula una consulta al almacén con un retraso de 300 milisegundos.
//
//Debe devolver un valor Boolean (true si hay stock, false si no). Supongamos que devuelve true.
//
//Paso 2 (procesarPago - Dependiente):
//
//Solo si hay stock (true), debe lanzar de forma asíncrona el proceso de pago. Si no hay stock, debe cancelarse o devolver un mensaje de error.
//
//        Pista: Como este paso depende del resultado del anterior, tendrás que usar thenCompose (o encadenarlo según el resultado).
//
//Simula una pasarela de pago que tarda 400 milisegundos y devuelve un código de transacción de tipo String (por ejemplo, "TXN-98765").
//
//Paso 3 (notificarCliente):
//
//Utiliza thenAccept para recibir el código de transacción (o el mensaje de que no hay stock) y mostrarlo por pantalla.
//
//Sincronización:
//
//Utiliza join() al final para esperar a que toda la cadena termine.

import java.util.concurrent.CompletableFuture;

public class ProcesarPAgoAsync {
    static void main() {
        CompletableFuture<Boolean> comprobarStock = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(500);
                return true;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<String> procesarPago = comprobarStock.thenCompose(condicion -> {
            if (!condicion) {
                // Si no hay stock, devolvemos un futuro que ya viene con este mensaje
                return CompletableFuture.completedFuture("No hay stock disponible");
            } else {
                // Si hay stock, lanzamos una NUEVA tarea asíncrona para la pasarela de pago (tarda 400ms)
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(400);
                        return "TXN-08333 (Pago exitoso)";
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });

        CompletableFuture<Void> resultado = procesarPago.thenAccept(txt -> System.out.println(txt));

        resultado.join();
    }
}
