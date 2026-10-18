package Threads.Ejercicios.SimulacroExam;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Procesamiento de pagos — 30 minutos (3,5 puntos)
public class ProcesoPagos {

    // Simulamos los métodos
    public static boolean comprobarSaldo() {
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        return true; // Cambiar según el escenario a probar
    }

    public static boolean validarTarjeta() {
        try { Thread.sleep(1200); } catch (InterruptedException e) {}
        return true; // Cambiar según el escenario a probar
    }

    public static void main(String[] args) {
        // 1. Executor propio
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            // 2. Ejecutar operaciones en paralelo con supplyAsync
            CompletableFuture<Boolean> saldoFuture = CompletableFuture.supplyAsync(() -> comprobarSaldo(), executor);
            CompletableFuture<Boolean> tarjetaFuture = CompletableFuture.supplyAsync(() -> validarTarjeta(), executor);

            // 3. Combinar resultados con thenCombine, timeout de 4 segundos y control de excepciones
            CompletableFuture<Void> procesoPago = saldoFuture
                    .thenCombine(tarjetaFuture, (saldo, tarjeta) -> {
                        // 4. Evaluar condiciones
                        if (saldo && tarjeta) {
                            System.out.println("Pago aprobado");
                        } else {
                            System.out.println("Pago rechazado");
                        }
                        return (Void) null; // <-- Solucionado el tipo Void aquí
                    })
                    .orTimeout(4, TimeUnit.SECONDS) // Timeout global de 4 segundos
                    .exceptionally(ex -> {
                        // Manejo de excepciones (por si salta error o timeout)
                        System.out.println("Pago rechazado (Error o Timeout): " + ex.getMessage());
                        return null;
                    });

            // Esperar a que termine la cadena asíncrona para que el main no muera antes
            procesoPago.join();

        } finally {
            // 5. Cerrar el executor correctamente
            executor.shutdown();
        }
    }
}