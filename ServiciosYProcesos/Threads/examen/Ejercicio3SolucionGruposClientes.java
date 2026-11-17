package Threads.examen;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Ejercicio3SolucionGruposClientes {

    enum TipoPlato {
        ENSALADA(2000, 850),
        PASTA(3000, 1200),
        PIZZA(4000, 100),
        CARNE(5000, 800);
        final int tiempoMs;
        final int precioCentimos;

        TipoPlato(int tiempoMs, int precioCentimos) {
            this.tiempoMs = tiempoMs;
            this.precioCentimos = precioCentimos;
        }

        static TipoPlato aleatorio(Random r) {
            return values()[r.nextInt(values().length)];
        }
    }

    // NUEVO: Añadimos el buzón del grupo para recibir los platos colectivamente
    record Pedido(int grupoId, int comensalId, TipoPlato plato, long horaCreacionMs, BlockingQueue<TipoPlato> buzonGrupo) {}

    // --- Constantes del enunciado ---
    static final int COCINEROS = 3;
    static final int CAMAREROS = 5;
    static final int TOTAL_GRUPOS = 40; // Número de hilos/grupos que llegarán
    static final int CAPACIDAD_MESA = 10;
    static final long LLEGADA_GRUPO_MS = 600;
    static final long TIEMPO_CAMARERO_MS = 1000;

    // --- Recursos compartidos ---
    static final BlockingQueue<Pedido> mesa = new ArrayBlockingQueue<>(CAPACIDAD_MESA);
    static final CountDownLatch gruposAtendidos = new CountDownLatch(TOTAL_GRUPOS);
    static final BlockingQueue<Pedido> platosListos = new ArrayBlockingQueue<>(30);
    static final BlockingQueue<Pedido> colaClientes = new ArrayBlockingQueue<>(100);
    static final Semaphore mesasRestaurante = new Semaphore(CAPACIDAD_MESA, true);

    // --- Estadisticas ---
    static final AtomicInteger platosServidos = new AtomicInteger();
    static final AtomicLong esperaTotalMs = new AtomicLong();
    static final AtomicInteger mesaLlenaCount = new AtomicInteger();
    static final AtomicLong cocinerosEsperandoMs = new AtomicLong();
    static final AtomicLong totalRecaudado = new AtomicLong(0);
    static volatile boolean restauranteAbierto = true;

    static String ahora() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== RESTAURANTE CONCURRENTE (GRUPOS) ===");
        System.out.println("Iniciando servicio con " + COCINEROS + " cocineros y "
                + CAMAREROS + " camareros...\n");

        Thread[] hilosCocineros = new Thread[COCINEROS];
        Thread[] hilosCamareros = new Thread[CAMAREROS];

        // --- Cocineros (consumidores) ---
        for (int i = 1; i <= COCINEROS; i++) {
            final int id = i;
            hilosCocineros[i - 1] = Thread.ofPlatform().name("cocinero-" + id).start(() -> {
                try {
                    while (restauranteAbierto || !mesa.isEmpty()) {
                        long t0 = System.currentTimeMillis();
                        Pedido p = mesa.take();
                        cocinerosEsperandoMs.addAndGet(System.currentTimeMillis() - t0);

                        Thread.sleep(p.plato().tiempoMs); // Cocina el plato
                        esperaTotalMs.addAndGet(System.currentTimeMillis() - p.horaCreacionMs());
                        platosServidos.incrementAndGet();

                        System.out.println("[" + ahora() + "] Cocinero-" + id + " termina "
                                + p.plato() + " para Grupo-" + p.grupoId() + " (comensal " + p.comensalId() + ")");
                        totalRecaudado.addAndGet(p.plato().precioCentimos);

                        // Dejamos el plato listo en la bandeja para que el camarero con prioridad lo recoja
                        platosListos.put(p);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // --- Camareros (prioridad absoluta para servir platos listos) ---
        for (int i = 1; i <= CAMAREROS; i++) {
            final int id = i;
            hilosCamareros[i - 1] = Thread.ofPlatform().name("camarero-" + id).start(() -> {
                try {
                    while (restauranteAbierto || !mesa.isEmpty() || !platosListos.isEmpty()) {

                        // 1. PRIORIDAD: ¿Hay platos listos en el mostrador?
                        Pedido platoListo = platosListos.poll();

                        if (platoListo != null) {
                            Thread.sleep(TIEMPO_CAMARERO_MS);
                            System.out.println("[" + ahora() + "] Camarero-" + id
                                    + " sirve " + platoListo.plato() + " a Grupo-" + platoListo.grupoId());

                            // Entregamos el plato al buzón compartido del grupo
                            platoListo.buzonGrupo().put(platoListo.plato());

                        } else {
                            // 2. SI NO HAY PLATOS LISTOS: Tomamos nuevos pedidos de la cola
                            Pedido p = colaClientes.poll();

                            if (p != null) {
                                Thread.sleep(TIEMPO_CAMARERO_MS);
                                System.out.println("[" + ahora() + "] Camarero-" + id
                                        + " toma pedido de Grupo-" + p.grupoId());

                                if (mesa.remainingCapacity() == 0) {
                                    mesaLlenaCount.incrementAndGet();
                                }
                                mesa.put(p); // Metemos el pedido en la cocina
                            } else {
                                Thread.sleep(100);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // --- Grupos de Clientes (hilos virtuales) ---
        Thread generadorGrupos = Thread.ofPlatform().name("puerta").start(() -> {
            Random rand = new Random();
            for (int i = 1; i <= TOTAL_GRUPOS; i++) {
                final int grupoId = i;

                // Cada grupo es un hilo virtual independiente
                Thread.ofVirtual().start(() -> {
                    try {
                        System.out.println("[" + ahora() + "] Grupo-" + String.format("%02d", grupoId) + " llega a la puerta.");

                        // 1. Espera mesa libre durante un tiempo límite (ej: 2 segundos)
                        if (!mesasRestaurante.tryAcquire(2000, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                            System.out.println("[" + ahora() + "] Grupo-" + String.format("%02d", grupoId) + " se va sin cenar (sin mesa).");
                            return;
                        }

                        // 2. Se sientan (tamaño aleatorio del grupo entre 2 y 4 personas)
                        int tamanoGrupo = 2 + rand.nextInt(3);
                        System.out.println("[" + ahora() + "] Grupo-" + String.format("%02d", grupoId) + " (" + tamanoGrupo + " personas) se sienta.");

                        // Buzón compartido para todo el grupo del tamaño exacto de sus comensales
                        BlockingQueue<TipoPlato> buzonGrupo = new ArrayBlockingQueue<>(tamanoGrupo);

                        // 3. El camarero toma la comanda (un plato por persona) y lo manda a cocina
                        for (int c = 1; c <= tamanoGrupo; c++) {
                            TipoPlato plato = TipoPlato.aleatorio(rand);
                            System.out.println("[" + ahora() + "] Grupo-" + grupoId + " (comensal " + c + ") pide " + plato);
                            colaClientes.put(new Pedido(grupoId, c, plato, System.currentTimeMillis(), buzonGrupo));
                        }

                        // 4. El grupo espera a que le sirvan TODOS los platos de su comanda
                        for (int c = 1; c <= tamanoGrupo; c++) {
                            TipoPlato recibido = buzonGrupo.take(); // Se bloquea hasta que llegue un plato del grupo
                            System.out.println("[" + ahora() + "] -> Grupo-" + grupoId + " recibe un plato (" + recibido + "). Faltan " + (tamanoGrupo - c));
                        }

                        // 5. Comen todos juntos
                        System.out.println("[" + ahora() + "] 🍽️ Grupo-" + grupoId + " tiene TODOS los platos y empieza a comer.");
                        Thread.sleep(1500); // Tiempo de comida del grupo

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        // 6. Liberan la mesa y avisan de que han terminado su ciclo
                        mesasRestaurante.release();
                        gruposAtendidos.countDown();
                    }
                });

                try {
                    Thread.sleep(LLEGADA_GRUPO_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        gruposAtendidos.await(); // Espera a que todos los grupos terminen (cenados o marchados)
        generadorGrupos.join();

        // Cierre controlado y ordenado
        restauranteAbierto = false;

        for (Thread c : hilosCocineros) c.interrupt();
        for (Thread cam : hilosCamareros) cam.interrupt();

        for (Thread camarero : hilosCamareros) {
            try { camarero.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        for (Thread cocinero : hilosCocineros) {
            try { cocinero.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        // --- Estadísticas Finales ---
        System.out.println("\n--- ESTADISTICAS FINALES ---");
        System.out.println("Grupos atendidos: " + TOTAL_GRUPOS + "/" + TOTAL_GRUPOS + " ✅");
        System.out.println("Total platos servidos: " + platosServidos.get());
        System.out.println("Tiempo promedio de espera por plato: "
                + (esperaTotalMs.get() / 1000.0 / Math.max(1, platosServidos.get())) + "s");
        System.out.println("Mesa llena (veces): " + mesaLlenaCount.get());
        System.out.println("Cocineros esperando (tiempo acumulado): "
                + (cocinerosEsperandoMs.get() / 1000) + "s total");
        System.out.println("Recaudación total: " + (totalRecaudado.get() / 100.0) + " €");
    }
}