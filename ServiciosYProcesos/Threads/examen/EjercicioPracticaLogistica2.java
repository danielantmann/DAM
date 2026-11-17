package Threads.examen;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

// --- CLASE QUE ENCAPSULA TODO EL ESTADO COMPARTIDO (Apuntando a EjercicioPracticaLogistica2) ---
class CentroLogisticoContext {
    // --- Constantes del enunciado ---
    final int operarios = 4;
    final int robots = 2;
    final int totalClientes = 50;
    final int capacidadCinta = 8;
    final long llegadaClienteMs = 300;
    final long tiempoOperarioMs = 800;

    // --- Recursos compartidos corregidos a EjercicioPracticaLogistica2 ---
    final BlockingQueue<EjercicioPracticaLogistica2.Paquete> cintaAdmision = new ArrayBlockingQueue<>(capacidadCinta);
    final CountDownLatch paquetesProcesados = new CountDownLatch(totalClientes);
    final BlockingQueue<EjercicioPracticaLogistica2.Paquete> colaClientes = new ArrayBlockingQueue<>(totalClientes);
    final Semaphore semaphoreCinta = new Semaphore(capacidadCinta, true);
    final BlockingQueue<EjercicioPracticaLogistica2.Paquete> paquetesTerminados = new ArrayBlockingQueue<>(10);

    // --- Estadisticas ---
    final AtomicInteger cintaLlenaCount = new AtomicInteger();
    final AtomicLong totalIngresado = new AtomicLong(0);
    final AtomicInteger paqueteCompletado = new AtomicInteger();
    final AtomicInteger paqueteNoAdmitido = new AtomicInteger();

    volatile boolean centroAbierto = true;

    String ahora() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}

public class EjercicioPracticaLogistica2 {

    enum TipoPaquete {
        ESTANDAR(1000, 500, 2),  // 1 segundo, 5.00 €
        EXPRESS(1500, 1200, 4),  // 1.5 segundos, 12.00 €
        FRAGIL(2000, 2000, 5);   // 2 segundos, 20.00 €

        final int tiempoProcesamientoMs;
        final int tarifaCentimos;
        final int peso;

        TipoPaquete(int tiempoProcesamientoMs, int tarifaCentimos, int peso) {
            this.tiempoProcesamientoMs = tiempoProcesamientoMs;
            this.tarifaCentimos = tarifaCentimos;
            this.peso = peso;
        }

        static TipoPaquete aleatorio(Random r) {
            return values()[r.nextInt(values().length)];
        }
    }

    record Paquete(int idCliente, TipoPaquete tipo, long horaCreacionMs, BlockingQueue<TipoPaquete> buzonPersonal) {}

    public static void main(String[] args) throws InterruptedException {
        CentroLogisticoContext centro = new CentroLogisticoContext();

        System.out.println("=== CENTRO LOGÍSTICO CONCURRENTE ===");
        System.out.println("Iniciando operaciones con " + centro.operarios + " operarios y "
                + centro.robots + " robots...\n");

        Thread[] hilosRobots = new Thread[centro.robots];
        Thread[] hilosOperarios = new Thread[centro.operarios];

        // --- Robots (consumidores) ---
        for (int i = 1; i <= centro.robots; i++) {
            final int id = i;
            hilosRobots[i - 1] = Thread.ofPlatform().name("robot-" + id).start(() -> {
                try {
                    while (centro.centroAbierto || !centro.cintaAdmision.isEmpty()) {
                        Paquete p = centro.cintaAdmision.take();
                        Thread.sleep(p.tipo().tiempoProcesamientoMs);

                        centro.paqueteCompletado.incrementAndGet();
                        centro.paquetesProcesados.countDown();

                        System.out.println("[" + centro.ahora() + "] Robot-" + id + " procesa paquete "
                                + p.tipo() + " del Cliente-" + p.idCliente());
                        centro.totalIngresado.addAndGet(p.tipo.tarifaCentimos);
                        centro.paquetesTerminados.put(p);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // --- Operarios (productores) ---
        for (int i = 1; i <= centro.operarios; i++) {
            final int id = i;
            hilosOperarios[i - 1] = Thread.ofPlatform().name("operario-" + id).start(() -> {
                try {
                    while (centro.centroAbierto || !centro.cintaAdmision.isEmpty() || !centro.paquetesTerminados.isEmpty()) {
                        Paquete paqueteListo = centro.paquetesTerminados.poll();

                        if (paqueteListo != null) {
                            Thread.sleep(centro.tiempoOperarioMs);
                            System.out.println("[" + centro.ahora() + "] Operario-" + id
                                    + " sirve " + paqueteListo.tipo + " a Cliente-" + paqueteListo.idCliente);
                            paqueteListo.buzonPersonal().put(paqueteListo.tipo());
                        } else {
                            Paquete p = centro.colaClientes.poll();

                            if (p != null) {
                                Thread.sleep(centro.tiempoOperarioMs);
                                System.out.println("[" + centro.ahora() + "] Operario-" + id
                                        + " registra paquete " + p.tipo());
                                if (centro.cintaAdmision.remainingCapacity() == 0) {
                                    centro.cintaLlenaCount.incrementAndGet();
                                }
                                centro.cintaAdmision.put(p);
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

        // --- Clientes (hilos virtuales) ---
        Thread generadorClientes = Thread.ofPlatform().name("puerta-clientes").start(() -> {
            for (int i = 1; i <= centro.totalClientes; i++) {
                final int id = i;

                Thread.ofVirtual().start(() -> {
                    try {
                        System.out.println("[" + centro.ahora() + "] Cliente-" + String.format("%03d", id) + " llega a la puerta.");

                        if (!centro.semaphoreCinta.tryAcquire(200, TimeUnit.MILLISECONDS)) {
                            System.out.println("[" + centro.ahora() + "] Cliente-" + String.format("%03d", id) + " se va (se cansó de esperar).");
                            centro.paqueteNoAdmitido.incrementAndGet();
                            return;
                        }

                        System.out.println("[" + centro.ahora() + "] Cliente-" + String.format("%03d", id) + " consigue la cinta.");
                        BlockingQueue<TipoPaquete> miBuzon = new ArrayBlockingQueue<>(1);
                        TipoPaquete tipo = TipoPaquete.aleatorio(new Random());

                        centro.colaClientes.put(new Paquete(id, tipo, System.currentTimeMillis(), miBuzon));

                        TipoPaquete paqueteProcesado = miBuzon.take();
                        System.out.println("[" + centro.ahora() + "] Cliente-" + id + " recibe su paquete " + paqueteProcesado);
                        Thread.sleep(centro.llegadaClienteMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        centro.semaphoreCinta.release();
                        centro.paquetesProcesados.countDown();
                    }
                });
            }
        });

        centro.paquetesProcesados.await();
        generadorClientes.join();

        centro.centroAbierto = false;

        for (Thread r : hilosRobots) r.interrupt();
        for (Thread o : hilosOperarios) o.interrupt();

        for (Thread r : hilosRobots) {
            try { r.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        for (Thread o : hilosOperarios) {
            try { o.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        // --- ESTADÍSTICAS FINALES ---
        System.out.println("\n--- ESTADISTICAS FINALES ---");
        System.out.println("Paquetes procesados: " + centro.totalClientes + "/" + centro.totalClientes + " ✅");
        System.out.println("Cinta llena (veces): " + centro.cintaLlenaCount.get());
        System.out.println("Dinero total ingresado: " + (centro.totalIngresado.get() / 100.0) + " €");
    }
}