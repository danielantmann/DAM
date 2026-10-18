package Threads.Ejercicios.PracticaHilos.CocinaMejorado;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class main {
    public enum TipoPlato {
        ENSALADA(2000, 850),    // 8.50 € (guardado en céntimos para evitar decimales)
        PASTA(3000, 1200),      // 12.00 €
        PIZZA(4000, 1450),      // 14.50 €
        CARNE(5000, 1800);      // 18.00 €

        private final int tiempoMs;
        private final int precioCentimos;

        TipoPlato(int tiempoMs, int precioCentimos) {
            this.tiempoMs = tiempoMs;
            this.precioCentimos = precioCentimos;
        }
        public int getTiempoMs() { return tiempoMs; }
        public int getPrecioCentimos() { return precioCentimos; }
    }

    public record Pedido(int clienteId, TipoPlato tipoPlato, long llegadaNs){};

    public static void main(String[] args) {
        int cantidadClientes = 100;
        int numCamareros = 5;
        int numCocineros = 3;
        int numMesas = 20; // Aforo del restaurante

        // Semáforo para controlar las mesas disponibles (cola de espera implícita)
        Semaphore mesasRestaurante = new Semaphore(numMesas, true);

        AtomicInteger platosPorTomar = new AtomicInteger(cantidadClientes);
        AtomicInteger platosPorCocinar = new AtomicInteger(cantidadClientes);
        AtomicInteger platosPorServir = new AtomicInteger(cantidadClientes);

        AtomicInteger contadorClientesAtendidos = new AtomicInteger(0);
        AtomicInteger platosServidos = new AtomicInteger(0);
        AtomicInteger clientesHicieronColaMesa = new AtomicInteger(0);
        AtomicInteger mesaLLenaVeces = new AtomicInteger(0);

        AtomicLong sumaEsperaClientesNs = new AtomicLong(0);
        AtomicLong gananciaTotalCentimos = new AtomicLong(0);

        ArrayBlockingQueue<Pedido> mesaClientes = new ArrayBlockingQueue<>(50); // Pedidos pendientes de tomar
        ArrayBlockingQueue<Pedido> mesaCocina = new ArrayBlockingQueue<>(10);    // Pedidos cocinándose
        ArrayBlockingQueue<Pedido> platosListos = new ArrayBlockingQueue<>(20);  // Platos listos para servir por camareros

        long inicioServicio = System.nanoTime();

        Thread[] hilosCamareros = new Thread[numCamareros];
        Thread[] hilosCocineros = new Thread[numCocineros];

        System.out.println("=== RESTAURANTE CONCURRENTE ABIERTO ===");
        System.out.println("Mesas: " + numMesas + " | Cocineros: " + numCocineros + " | Camareros: " + numCamareros);

        // 1. Clientes (Hilos virtuales)
        for (int i = 1; i <= cantidadClientes ; i++) {
            int clienteId = i;
            Thread.startVirtualThread(() -> {
                try {
                    Thread.sleep(500L * clienteId); // Llegada escalonada
                    String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

                    // Comprobar si hay mesas libres
                    if (!mesasRestaurante.tryAcquire()) {
                        clientesHicieronColaMesa.incrementAndGet();
                        System.out.println("[" + timestamp + "] Cliente-" + String.format("%03d", clienteId) + " llega pero el restaurante está lleno. Esperando mesa...");
                        mesasRestaurante.acquire(); // Bloquea hasta que se libre una mesa
                    }

                    System.out.println("[" + timestamp + "] Cliente-" + String.format("%03d", clienteId) + " se sienta en una mesa.");

                    TipoPlato plato = TipoPlato.values()[ThreadLocalRandom.current().nextInt(TipoPlato.values().length)];
                    Pedido pedido = new Pedido(clienteId, plato, System.nanoTime());

                    mesaClientes.put(pedido); // Deja el pedido para que lo tome el camarero

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 2. Camareros (Toman pedidos y sirven platos)
        for (int i = 0; i < numCamareros; i++) {
            int camareroId = i + 1;
            hilosCamareros[i] = new Thread(() -> {
                try {
                    while (true) {
                        // Intentan servir primero si hay platos listos, o toman pedidos si no
                        // Para simplificar, dividiremos sus tareas o usaremos bucles separados según toque:
                        if (platosPorTomar.get() > 0 && !mesaClientes.isEmpty()) {
                            // Tarea 1: Tomar pedido
                            Pedido pedido = mesaClientes.poll();
                            if (pedido != null) {
                                plazosRestarTomar(platosPorTomar);
                                String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                                System.out.println("[" + timestamp + "] Camarero-" + camareroId + " toma pedido " + pedido.tipoPlato() + " de Cliente-" + String.format("%03d", pedido.clienteId()));
                                Thread.sleep(1000); // Tiempo de toma de pedido

                                if (!mesaCocina.offer(pedido)) {
                                    mesaCocina.put(pedido);
                                    mesaLLenaVeces.incrementAndGet();
                                }
                            }
                        }

                        // Tarea 2: Servir plato listo
                        Pedido platoListo = platosListos.poll();
                        if (platoListo != null) {
                            Thread.sleep(1000); // Tiempo de servir
                            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                            System.out.println("[" + timestamp + "] Camarero-" + camareroId + " sirve " + platoListo.tipoPlato() + " a Cliente-" + String.format("%03d", platoListo.clienteId()));

                            platosServidos.incrementAndGet();
                            contadorClientesAtendidos.incrementAndGet();
                            sumaEsperaClientesNs.addAndGet(System.nanoTime() - platoListo.llegadaNs());
                            gananciaTotalCentimos.addAndGet(platoListo.tipoPlato().getPrecioCentimos());

                            mesasRestaurante.release(); // El cliente come, paga y libera la mesa
                        }

                        if (platosServidos.get() >= cantidadClientes) {
                            break;
                        }
                        Thread.sleep(50); // Pequeño respiro para no saturar CPU
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            hilosCamareros[i].start();
        }

        // 3. Cocineros (Cocinan y dejan el plato listo)
        for (int i = 0; i < numCocineros; i++) {
            int cocineroId = i + 1;
            hilosCocineros[i] = new Thread(() -> {
                try {
                    while (platosPorCocinar.getAndDecrement() > 0) {
                        Pedido pedido = mesaCocina.take();
                        Thread.sleep(pedido.tipoPlato().getTiempoMs());

                        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                        System.out.println("[" + timestamp + "] Cocinero-" + cocineroId + " termina " + pedido.tipoPlato() + " para Cliente-" + String.format("%03d", pedido.clienteId()));

                        platosListos.put(pedido); // Pone el plato listo para que el camarero lo sirva
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            hilosCocineros[i].start();
        }

        // Esperar a los camareros y cocineros
        for (Thread camarero : hilosCamareros) {
            try { camarero.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        for (Thread cocinero : hilosCocineros) {
            try { cocinero.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        // Estadísticas Finales
        double promedioEspera = sumaEsperaClientesNs.get() / (double) cantidadClientes / 1_000_000_000.0;
        double gananciasEuros = gananciaTotalCentimos.get() / 100.0;

        System.out.println("\n--- ESTADÍSTICAS FINALES DEL RESTAURANTE ---");
        System.out.println("Clientes atendidos con éxito: " + contadorClientesAtendidos.get() + "/" + cantidadClientes + " ✅");
        System.out.println("Clientes que hicieron cola de espera para mesa: " + clientesHicieronColaMesa.get());
        System.out.println("Platos servidos: " + platosServidos.get());
        System.out.println("Ganancias totales del día: " + String.format("%.2f", gananciasEuros) + " €");
        System.out.println("Veces que la cocina se llenó: " + mesaLLenaVeces.get());
        System.out.println("Tiempo promedio de espera del cliente: " + String.format("%.1f", promedioEspera) + "s");
    }

    private static synchronized void plazosRestarTomar(AtomicInteger contador) {
        contador.decrementAndGet();
    }
}