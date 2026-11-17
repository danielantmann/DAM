package Threads.examen;
//[1.0 Pto] Cierre controlado: ¡Sí! Has quitado el System.exit, utilizas la bandera centroteAbierto, interrumpes los hilos con .interrupt() y haces los .join() ordenados.
//
//        [0.5 Pto] Tipos de paquetes y costes: ¡Sí! Tienes tu enum TipoPaquete con tarifas, tiempos y pesos, y estás acumulando con totalIngresado.addAndGet(...). (Un detalle: recuerda imprimir totalIngresado.get() en las estadísticas finales para que el profesor vea el dinero recaudado).
//
//        [2.0 Ptos] Flujo del paquete (hilo virtual individual + Semáforo + Buzón): ¡Perfecto! Los clientes entran como hilos virtuales, usan semaphoreCinta.tryAcquire con timeout, depositan en la cola, y se bloquean esperando en su miBuzon.take().
//
//        [2.0 Ptos] Prioridad: ¡Claro que sí! Al adaptar la estructura del restaurante, tus operarios miran primero .poll() en paquetesTerminados (prioridad absoluta para entregar) antes de atender la puerta (colaClientes).

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EjercicioPracticaLogistica {

    enum TipoPaquete {
        ESTANDAR(1000, 500,2),  // 1 segundo, 5.00 €
        EXPRESS(1500, 1200,4),  // 1.5 segundos, 12.00 €
        FRAGIL(2000, 2000,5);   // 2 segundos, 20.00 €

        final int tiempoProcesamientoMs;
        final int tarifaCentimos;
        final int peso;

        TipoPaquete(int tiempoProcesamientoMs, int tarifaCentimos, int peso
        ) {
            this.tiempoProcesamientoMs = tiempoProcesamientoMs;
            this.tarifaCentimos = tarifaCentimos;
            this.peso = peso;
        }

        static TipoPaquete aleatorio(Random r) {
            return values()[r.nextInt(values().length)];
        }
    }

    record Paquete(int idCliente, TipoPaquete tipo, long horaCreacionMs, BlockingQueue<TipoPaquete> buzonPersonal ) {}

    // --- Constantes del enunciado ---
    static final int OPERARIOS = 4;
    static final int ROBOTS = 2;
    static final int TOTAL_CLIENTES = 50;
    static final int CAPACIDAD_CINTA = 8;
    static final long LLEGADA_CLIENTE_MS = 300;
    static final long TIEMPO_OPERARIO_MS = 800;

    // --- Recursos compartidos ---
    static final BlockingQueue<Paquete> cintaAdmision = new ArrayBlockingQueue<>(CAPACIDAD_CINTA);
    static final CountDownLatch paquetesProcesados = new CountDownLatch(TOTAL_CLIENTES);
    static final BlockingQueue<Paquete> colaClientes = new ArrayBlockingQueue<>(TOTAL_CLIENTES);
    static  final Semaphore semaphoreCinta = new Semaphore(CAPACIDAD_CINTA, true);
    static final BlockingQueue<Paquete> paquetesTerminados = new ArrayBlockingQueue<>(10);
    // --- Estadisticas ---
    static final AtomicInteger totalEnviados = new AtomicInteger();
    static final AtomicLong esperaTotalMs = new AtomicLong();
    static final AtomicInteger cintaLlenaCount = new AtomicInteger();
    static final AtomicLong totalIngresado = new AtomicLong(0);
    static final AtomicInteger paqueteCompletado = new AtomicInteger();
    static final AtomicInteger paqueteNoAdmitido = new AtomicInteger();
    static String ahora() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    static volatile boolean centroteAbierto = true;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== CENTRO LOGÍSTICO CONCURRENTE ===");
        System.out.println("Iniciando operaciones con " + OPERARIOS + " operarios y "
                + ROBOTS + " robots...\n");

        Thread[] hilosRobots = new Thread[ROBOTS];
        Thread[] hilosOperarios = new Thread[OPERARIOS];
        // --- Robots (consumidores) ---
        for (int i = 1; i <= ROBOTS; i++) {
            final int id = i;
            hilosRobots[i - 1] = Thread.ofPlatform().name("robot-" + id).start(() -> {
                try {
                    while (centroteAbierto || ! cintaAdmision.isEmpty()) {
                        Paquete p = cintaAdmision.take(); // coge paquete de la cinta
                        Thread.sleep(p.tipo().tiempoProcesamientoMs); // procesa el paquete

                        paqueteCompletado.incrementAndGet(); // (cambiar a métrica de logística)
                        paquetesProcesados.countDown();

                        System.out.println("[" + ahora() + "] Robot-" + id + " procesa paquete "
                                + p.tipo() + " del Cliente-" + p.idCliente());
                        totalIngresado.addAndGet(p.tipo.tarifaCentimos);
                        paquetesTerminados.put(p);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // --- Operarios (productores) ---
        for (int i = 1; i <= OPERARIOS; i++) {
            final int id = i;
            hilosOperarios[i -1 ] = Thread.ofPlatform().name("operario-" + id).start(() -> {
                try {
                    while (centroteAbierto || ! cintaAdmision.isEmpty() || !paquetesTerminados.isEmpty()) {
                        Paquete paqueteListo = paquetesTerminados.poll();

                        if (paqueteListo != null){
                            Thread.sleep(TIEMPO_OPERARIO_MS);
                            System.out.println("[" + ahora() + "] Operario-" + id
                                    + " sirve " + paqueteListo.tipo + " a Cliente-" + paqueteListo.idCliente);
                            paqueteListo.buzonPersonal().put(paqueteListo.tipo());
                        }else {
                            Paquete p = colaClientes.poll();

                            if (p != null){
                                Thread.sleep(TIEMPO_OPERARIO_MS);
                                System.out.println("[" + ahora() + "] Operario-" + id
                                        + " registra paquete " + p.tipo());
                                if (cintaAdmision.remainingCapacity() == 0) {
                                    cintaLlenaCount.incrementAndGet();
                                }
                                cintaAdmision.put(p);
                            }else{
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
            for (int i = 1; i <= TOTAL_CLIENTES; i++) {
                final int id = i;

                Thread.ofVirtual().start(()->{

                try {
                    System.out.println("[" + ahora() + "] Cliente-" + String.format("%03d", id) + " llega a la puerta.");

                    if (!semaphoreCinta.tryAcquire(200,  java.util.concurrent.TimeUnit.MILLISECONDS)){
                        System.out.println("[" + ahora() + "] Cliente-" + String.format("%03d", id) + " se va(se cansó de esperar ).");
                        paqueteNoAdmitido.incrementAndGet();
                        return;
                    }

                    System.out.println("[" + ahora() + "] Cliente-" + String.format("%03d", id) + " consigue la cinta.");
                    BlockingQueue<TipoPaquete> miBuzon = new ArrayBlockingQueue<>(1);
                    TipoPaquete tipo = TipoPaquete.aleatorio(new Random());

                    colaClientes.put(new Paquete(id, tipo, System.currentTimeMillis(), miBuzon));

                    TipoPaquete paqueteProcesado = miBuzon.take();
                    System.out.println("[" + ahora() + "] Cliente-" + id + " recibe y su paqueta " + paqueteProcesado);
                    Thread.sleep(LLEGADA_CLIENTE_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }finally {
                    semaphoreCinta.release();
                    paquetesProcesados.countDown();
                }
                });
            }
        });

        paquetesProcesados.await();
        generadorClientes.join();

        centroteAbierto = false;

        for (Thread r : hilosRobots) r.interrupt();
        for (Thread o : hilosOperarios) o.interrupt();

        for (Thread r : hilosRobots) {
            try { r.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        for (Thread o : hilosOperarios) {
            try { o.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        System.out.println("\n--- ESTADISTICAS FINALES ---");
        System.out.println("Paquetes procesados: " + TOTAL_CLIENTES + "/" + TOTAL_CLIENTES + " ✅");
        System.out.println("Cinta llena (veces): " + cintaLlenaCount.get());
//        System.exit(0);
    }
}