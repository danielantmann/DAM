package Threads.Ejercicios.PracticaHilos.Ej03Cocina;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class main {
    public enum TipoPlato {
        ENSALADA(2000), PASTA(3000), PIZZA(4000), CARNE(5000);
        private final int tiempoMs;
        TipoPlato(int tiempoMs) { this.tiempoMs = tiempoMs; }
        public int getTiempoMs() { return tiempoMs; }
    }

    public record Pedido(int clienteId, TipoPlato tipoPlato, long llegadaNs){};

    public static void main(String[] args) {

        int cantidadClientes = 100;
        int numCamareros = 5;
        int numCocineros = 3;

        AtomicInteger platosPorTomar = new AtomicInteger(cantidadClientes);
        AtomicInteger platosPorCocinar = new AtomicInteger(cantidadClientes);
        AtomicInteger contadorClientesAtendidos = new AtomicInteger(0);
        AtomicInteger platosServidos = new AtomicInteger(0);
        AtomicInteger mesaLLena = new AtomicInteger(0);
        AtomicLong sumaEsperaClientesNs = new AtomicLong(0);   // suma de lo que esperó cada cliente
        AtomicLong esperaCocinerosNs   = new AtomicLong(0);
        AtomicLong trabajoNs = new AtomicLong(0);
        ArrayBlockingQueue<Pedido> mesaClientes = new ArrayBlockingQueue<>(100);
        ArrayBlockingQueue<Pedido> mesaCocina = new ArrayBlockingQueue<>(10);

        long inicioServicio = System.nanoTime();

        Thread[] hilosCamareros = new Thread[numCamareros];
        Thread[] hilosCocineros = new Thread[numCocineros];

        System.out.println("Restaurante abierto");

        // 1. Clientes (Hilos virtuales)
        for (int i = 1; i <= cantidadClientes ; i++) {
            int clienteId = i;
            Thread.startVirtualThread(() -> {
                try {
                    Thread.sleep(500L * clienteId);
                    TipoPlato plato = TipoPlato.values()[ThreadLocalRandom.current().nextInt(TipoPlato.values().length)];
                    Pedido pedido = new Pedido(clienteId, plato, System.nanoTime());
                    String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                    System.out.println("[" + timestamp + "] Cliente-" + String.format("%03d", clienteId) + " pide " + plato);
                    mesaClientes.put(pedido);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 2. Camareros (Hilos normales, empezamos en i = 0 hasta < numCamareros)

        for (int i = 0; i < numCamareros; i++) {
            int camareroId = i + 1;
            hilosCamareros[i] = new Thread(() -> {
                try {
                        while (platosPorTomar.getAndDecrement()> 0){
                            Pedido pedido = mesaClientes.take();
                            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                            System.out.println("[" + timestamp + "] Camarero-" + camareroId + " toma pedido " + pedido.tipoPlato());
                            contadorClientesAtendidos.incrementAndGet();
                            Thread.sleep(1000);
                            if (!mesaCocina.offer(pedido)){
                                mesaCocina.put(pedido);
                                mesaLLena.incrementAndGet();
                            }

                        }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            hilosCamareros[i].start();
        }

        // 3. Cocineros (Hilos normales, empezamos en i = 0 hasta < numCocineros)

        for (int i = 0; i < numCocineros; i++) {
            int cocineroId = i + 1;
            hilosCocineros[i] = new Thread(() -> {
                try {

                    while (platosPorCocinar.getAndDecrement() > 0){
                        long antes = System.nanoTime();
                        Pedido pedido = mesaCocina.take();
                        esperaCocinerosNs.addAndGet(System.nanoTime() - antes);

                        Thread.sleep(pedido.tipoPlato().getTiempoMs());
                        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                        platosServidos.incrementAndGet();
                        trabajoNs.addAndGet(pedido.tipoPlato().getTiempoMs() * 1_000_000L);
                        System.out.println("[" + timestamp + "] Cocinero-" + cocineroId + " termina " + pedido.tipoPlato()
                                + " para Cliente-" + String.format("%03d", pedido.clienteId()));
                        sumaEsperaClientesNs.addAndGet(System.nanoTime() - pedido.llegadaNs());
                    }


                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            hilosCocineros[i].start();
        }

        // Esperar a los camareros
        for (Thread camarero : hilosCamareros) {
            try {
                camarero.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Esperar a los cocineros
        for (Thread cocinero : hilosCocineros) {
            try {
                cocinero.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long duracionNs = System.nanoTime() - inicioServicio;

// 1. promedio: suma de esperas / nº de clientes, y pasar a segundos
        double promedioEspera = sumaEsperaClientesNs.get() / platosServidos.get() / 1_000_000_000.0;

// 2. cocineros esperando: ya es una suma, solo pasar a segundos
        double esperaCocineros = esperaCocinerosNs.get() / 1_000_000_000.0;

// 3. eficiencia: tiempo cocinando de verdad / tiempo que podían estar cocinando
        double eficiencia = (double) trabajoNs.get() / ((double) numCocineros * duracionNs) * 100;

        System.out.println("");
        System.out.println("--- ESTADÍSTICAS FINALES ---");
        System.out.println("Restaurante cerrado y servicio completado.");
        System.out.println("Clientes atendidos: " + contadorClientesAtendidos.get() + "/" + cantidadClientes
                + (contadorClientesAtendidos.get() == cantidadClientes ? " ✅" : " ❌"));
        System.out.println("platos servidos:" + platosServidos.get() );
        System.out.println("Mesa llena(veces): " + mesaLLena.get());
        System.out.println("Tiempo promedio de espera: " + String.format("%.1f", promedioEspera) + "s");
        System.out.println("Cocineros esperando (tiempo): " + String.format("%.0f", esperaCocineros) + "s total");
        System.out.println("Eficiencia: " + String.format("%.0f", eficiencia) + "%");
    }
}