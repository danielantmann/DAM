package Threads.Ejercicios.SimulacroExam;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingSimulacion {

    // Definimos el tipo de vehículo sencillo
    enum TipoVehiculo {
        NORMAL(1.0); // 1€ por minuto (o por fracción de ms para la simulación)

        private final double tarifaPorMinuto;
        TipoVehiculo(double tarifa) { this.tarifaPorMinuto = tarifa; }
        public double getTarifa() { return tarifaPorMinuto; }
    }

    public static void main(String[] args) {
        final int PLAZAS_TOTALES = 5; // Imagínate que son 20, ponemos 5 para probar rápido
        final int CAPACIDAD_COLA = 3; // Máximo 3 coches esperando en la cola de entrada
        final int TOTAL_COCHES = 15;  // Coches que intentarán entrar

        // 1. Semáforo para controlar las plazas físicas del parking
        Semaphore plazasParking = new Semaphore(PLAZAS_TOTALES);

        // 2. Cola de espera física para los coches que llegan y no hay plaza libre al momento
        ArrayBlockingQueue<String> colaEspera = new ArrayBlockingQueue<>(CAPACIDAD_COLA);

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor(); // O newFixedThreadPool si no usáis hilos virtuales
        Random random = new Random();
        AtomicInteger recaudacionTotal = new AtomicInteger(0);

        // Simula la llegada de los coches
        for (int i = 1; i <= TOTAL_COCHES; i++) {
            int idCoche = i;

            executor.submit(() -> {
                String nombreCoche = "Coche-" + idCoche;
                System.out.println("🚗 " + nombreCoche + " llega al parking.");

                // Intentamos meter el coche en la cola de espera del parking
                // offer con timeout o al instante: si la cola está llena, devuelve false y "se piró"
                boolean entraEnCola = colaEspera.offer(nombreCoche);

                if (!entraEnCola) {
                    System.out.println("❌ " + nombreCoche + " ve el parking y la cola LLENOS. Se marcha triste.");
                    return; // Fin del hilo para este coche
                }

                try {
                    System.out.println("⏳ " + nombreCoche + " está esperando en la cola...");

                    // Ahora el coche intenta conseguir una plaza física del semáforo.
                    // Usamos tryAcquire con un tiempo máximo de espera de 3 segundos por si se cansa en la cola.
                    boolean consiguePlaza = plazasParking.tryAcquire(3, TimeUnit.SECONDS);

                    if (consiguePlaza) {
                        // Ya salió de la cola y entró al parking
                        colaEspera.remove(nombreCoche); // Lo sacamos de la cola de espera

                        try {
                            long tiempoInicio = System.currentTimeMillis();
                            System.out.println("✅ " + nombreCoche + " ¡Ha aparcado en una plaza!");

                            // Simulamos el tiempo de estancia (en el examen puedes poner menos tiempo para probar)
                            int tiempoEstancia = random.nextInt(1000, 3000); // 1 a 3 segundos
                            Thread.sleep(tiempoEstancia);

                            long tiempoTotal = System.currentTimeMillis() - tiempoInicio;

                            // Cálculo del importe (ej: 1 céntimo por cada 100 ms de estancia)
                            int coste = (int) (tiempoTotal / 100);
                            recaudacionTotal.addAndGet(coste);

                            System.out.println("🏁 " + nombreCoche + " sale del parking. Estuvo " + tiempoTotal + " ms. Pagó: " + coste + "€");

                        } finally {
                            // MUY IMPORTANTE: Liberar la plaza al salir
                            plazasParking.release();
                        }
                    } else {
                        // Pasó el tiempo máximo de espera y no consiguió plaza
                        colaEspera.remove(nombreCoche);
                        System.out.println("⌛ " + nombreCoche + " se cansó de esperar en la cola y se marchó.");
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            // Pequeña pausa entre la llegada de los coches
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {}
        }

        executor.shutdown();
        try {
            executor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}

        System.out.println("\n===== RESUMEN FINAL DEL PARKING =====");
        System.out.println("Recaudación total: " + recaudacionTotal.get() + " €");
        System.out.println("======================================");
    }
}