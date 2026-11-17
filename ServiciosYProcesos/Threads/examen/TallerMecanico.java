package Threads.examen;


//[1.0 Pto] Cierre controlado: El cierre del taller deberá de ser controlado y ordenado. Utiliza una bandera (tallerAbierto), interrumpe los hilos de mecánicos y elevadores con .interrupt() de forma limpia, y asegúrate de hacer los .join() correspondientes sin usar System.exit().
//
//        [0.5 Pto] Tipos de averías y costes: Define un enum TipoAveria (por ejemplo: ELECTRICA, MECANICA, NEUMATICOS) que almacene diferentes tarifas en céntimos, tiempos de reparación en milisegundos y nivel de gravedad. Acumula el dinero total recaudado utilizando un AtomicLong con addAndGet(...) y recuerda imprimir el total en las estadísticas finales.
//
//        [2.0 Ptos] Flujo del cliente (hilo virtual + Semáforo + Buzón personal):
//
//Los clientes llegan al taller como hilos virtuales.
//
//Intentan conseguir un hueco en la recepción utilizando un semáforo con un tiempo de espera límite (semaphoreRecepcion.tryAcquire con timeout).
//
//Si se agota el tiempo y no consiguen acceder, se van (se incrementa la métrica de clientes frustrados).
//
//Si consiguen entrar, entregan las llaves, las depositan en la cola de admisión y se quedan bloqueados esperando su vehículo en su buzón personal (miBuzon.take()).
//
//        [2.0 Ptos] Prioridad: Los mecánicos tienen PRIORIDAD ABSOLUTA para entregar los vehículos reparados: antes de atender la puerta o coger un nuevo coche de la cola de espera, comprueban siempre primero con .poll() si hay vehículos listos en la zona de entrega para devolvérselos a los clientes.
//


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class TallerContext{
    final int mecanicos = 4;
    final int elevadores= 2;
    final int totalClientes = 50;
    final int capacidadRecepcion = 8;
    final int capacidadTerminados = 10;
    final long llegadaClienteMs = 300;
    final long tiempoMecanicosMs = 800;

    final BlockingQueue<Averia> mostrador = new ArrayBlockingQueue<>(capacidadRecepcion);
    final BlockingQueue<Averia> colaClientes = new ArrayBlockingQueue<>(totalClientes);
    final BlockingQueue<Averia> averiasTerminados = new ArrayBlockingQueue<>(capacidadRecepcion);
    final Semaphore semaphoreMostrador = new Semaphore(capacidadRecepcion,true);
    final CountDownLatch averiasProcesadas = new CountDownLatch(totalClientes);

    final AtomicInteger mostradorLLenoCount = new AtomicInteger();
    final AtomicLong totalIngresado = new AtomicLong(0);
    final AtomicInteger averiasCompletadas = new AtomicInteger();
    final AtomicInteger averiasNoAdmitidas = new AtomicInteger();

    volatile boolean centroAbierto = true;

    String ahora() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}

enum TipoAveria{
    ELECTRICA(1000, 500, 6),
    MECANICA(1500, 1200, 12 ),
    NEUMATICOS(2000, 1500, 1);

    final int tiempoProcesamiento;
    final int tarifaCent;
    final int nivelAveria;

    TipoAveria(int tiempoProcesamiento, int tarifaCent, int nivelAveria) {
        this.tiempoProcesamiento = tiempoProcesamiento;
        this.tarifaCent = tarifaCent;
        this.nivelAveria = nivelAveria;
    }
    static  TipoAveria aleatorio(Random r){
        return values()[r.nextInt(values().length)];
    }
}

record  Averia(int idCliente, TipoAveria tipo, long horaCreacion, BlockingQueue<TipoAveria> buzonPersonal){}

public class TallerMecanico {
    static void main() {
        TallerContext centro = new TallerContext();

        System.out.println("=== CENTRO TALLER CONCURRENTE ===");
        System.out.println("Iniciando operaciones con " + centro.mecanicos + " tecnicos y "
                + centro.elevadores + " impresoras...\n");

        Thread[] hilosMecanicos = new Thread[centro.mecanicos];
        Thread[] hilosElevadores = new Thread[centro.elevadores];

        // --- Elevadores (consumidores) ---

        for (int i = 1; i <= centro.elevadores; i++) {
            final int id = i;

            hilosElevadores[i - 1] =  Thread.ofPlatform().name("elevador-" + id).start(() -> {
                try {
                    while (centro.centroAbierto || !centro.mostrador.isEmpty()){
                        Averia averia = centro.mostrador.take();

                        Thread.sleep(averia.tipo().tiempoProcesamiento);

                        centro.averiasCompletadas.incrementAndGet();

                        System.out.println("[" + centro.ahora() + "] elvador-" + id + " procesa averia "
                                + averia.tipo() + " del Cliente-" + averia.idCliente());

                        centro.totalIngresado.addAndGet(averia.tipo().tarifaCent);
                        centro.averiasTerminados.put(averia);

                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // --- mecanicos (productores) ---

        for (int i = 1; i <= centro.mecanicos ; i++) {
            final int id = i;

            hilosMecanicos[i - 1] = Thread.ofPlatform().name("mecanico-" + id).start(() -> {
                try {
                    while (centro.centroAbierto || ! centro.mostrador.isEmpty() || !centro.averiasTerminados.isEmpty()){
                        Averia averiaLista = centro.averiasTerminados.poll();

                        if (averiaLista != null){
                            Thread.sleep(centro.tiempoMecanicosMs);

                            System.out.println("[" + centro.ahora() + "] mecanico-" + id
                                    + " sirve " + averiaLista.tipo() + " a Cliente-" + averiaLista.idCliente());

                            averiaLista.buzonPersonal().put(averiaLista.tipo());
                        }else {
                            Averia averia = centro.colaClientes.poll();

                            if (averia != null){
                                Thread.sleep(centro.tiempoMecanicosMs);

                                System.out.println("[" + centro.ahora() + "] Mecanico-" + id
                                        + " registra averia " + averia.tipo());

                                if (centro.mostrador.remainingCapacity() == 0){
                                    centro.mostradorLLenoCount.incrementAndGet();
                                }

                                centro.mostrador.put(averia);

                            }else {
                                Thread.sleep(1000);
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
            for (int i = 1; i <= centro.totalClientes ; i++) {
                final int id = i;
                Thread.ofVirtual().start(()->{
                    System.out.println("[" + centro.ahora() + "] Cliente-" + String.format("%03d", id) + " llega a la puerta.");

                 try{
                     if (!centro.semaphoreMostrador.tryAcquire(2000, TimeUnit.MILLISECONDS)){
                         System.out.println("[" + centro.ahora() + "] Cliente-" + String.format("%03d", id) + " se va (se cansó de esperar).");
                         centro.averiasNoAdmitidas.incrementAndGet();
                         return;
                     }

                     System.out.println("[" + centro.ahora() + "] Cliente-" + String.format("%03d", id) + " consigue mostrador.");

                     BlockingQueue<TipoAveria> miBuzon = new ArrayBlockingQueue<>(1);


                     TipoAveria tipo = TipoAveria.aleatorio(new Random());

                     Thread.sleep(centro.llegadaClienteMs);


                     centro.colaClientes.put(new Averia(id, tipo, System.currentTimeMillis(), miBuzon));

                     TipoAveria averiaProcesada = miBuzon.take();

                     System.out.println("[" + centro.ahora() + "] Cliente-" + id + " recibe su averia " + averiaProcesada);
                 } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                 }finally {
                     centro.semaphoreMostrador.release();
                     centro.averiasProcesadas.countDown();
                 }
                });


            }
        });

        try {
            centro.averiasProcesadas.await();
            generadorClientes.join();
        } catch (InterruptedException e) {

            throw new RuntimeException(e);
        }

        centro.centroAbierto =false;


        for (Thread el : hilosElevadores) el.interrupt();
        for (Thread m : hilosMecanicos) m.interrupt();

        for (Thread el : hilosElevadores) {
            try { el.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        for (Thread m : hilosMecanicos) {
            try { m.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        // --- ESTADÍSTICAS FINALES ---
        System.out.println("\n--- ESTADISTICAS FINALES ---");
        System.out.println("Paquetes procesados: " + centro.totalClientes + "/" + centro.totalClientes + " ✅");
        System.out.println("Cinta llena (veces): " + centro.mostradorLLenoCount.get());
        System.out.println("Dinero total ingresado: " + (centro.totalIngresado.get() / 100.0) + " €");
    }
}
