package Threads.examen;
//[1.0 Pto] Cierre controlado: El cierre de la imprenta deberá de ser controlado y ordenado. Utiliza una bandera (imprentaAbierta), interrumpe los hilos de técnicos e impresoras con .interrupt() de forma limpia, y asegúrate de hacer los .join() correspondientes sin usar System.exit().
//
//        [0.5 Pto] Tipos de trabajos y costes: Define un enum TipoTrabajo (por ejemplo: FOTO, DOCUMENTO, POSTER) que almacene diferentes tarifas en céntimos, tiempos de impresión en milisegundos y número de páginas. Acumula el dinero total recaudado utilizando un AtomicLong con addAndGet(...) y recuerda imprimir el total en las estadísticas finales.
//
//        [2.0 Ptos] Flujo del cliente (hilo virtual + Semáforo + Buzón personal):
//
//Los clientes llegan al mostrador de la imprenta como hilos virtuales.
//
//Intentan conseguir un hueco en la cola de recepción utilizando un semáforo con un tiempo de espera límite (semaphoreMostrador.tryAcquire con timeout).
//
//Si se agota el tiempo y no consiguen acceder, se van (se incrementa la métrica de clientes frustrados).
//
//Si consiguen entrar, entregan su archivo, lo depositan en la cola de encargos y se quedan bloqueados esperando sus impresiones en su buzón personal (miBuzon.take()).
//
//        [2.0 Ptos] Prioridad: Los técnicos tienen PRIORIDAD ABSOLUTA para entregar los trabajos terminados: antes de atender la puerta o coger un nuevo encargo de la cola, comprueban siempre primero con .poll() si hay trabajos listos en la bandeja de recogida para entregárselos a los clientes.

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class ImprentaContext{
    final int tecnicos = 4;
    final int impresoras = 2;
    final int totalClientes = 60;
    final long llegadaClienteMs = 300;
    final long tiempoTecnicosMs = 800;
    final int capacidadMostrador = 8;
    final int capacidadTerminados = 10;


    final BlockingQueue<Impresion> mostrador = new ArrayBlockingQueue<>(capacidadMostrador);
    final BlockingQueue<Impresion> colaClientes = new ArrayBlockingQueue<>(totalClientes);
    final BlockingQueue<Impresion> documentosTerminados = new ArrayBlockingQueue<>(capacidadMostrador);
    final Semaphore semaphoreMostrador = new Semaphore(capacidadMostrador,true);
    final CountDownLatch impresionesProcesadas = new CountDownLatch(totalClientes);

    final AtomicInteger mostradorLLenoCount = new AtomicInteger();
    final AtomicLong totalIngresado = new AtomicLong(0);
    final AtomicInteger impresionesCompletadas = new AtomicInteger();
    final AtomicInteger impresionesNoAdmitidas = new AtomicInteger();

    volatile boolean centroAbierto = true;

    String ahora() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}

enum TipoImpresion{
    FOTO(1000, 500, 6),
    DOCUMENTO(1500, 1200, 12 ),
    POSTER(2000, 1500, 1);

    final int tiempoProcesamiento;
    final int tarifaCent;
    final int pagina;

    TipoImpresion(int tiempoProcesamiento, int tarifaCent, int pagina) {
        this.tiempoProcesamiento = tiempoProcesamiento;
        this.tarifaCent = tarifaCent;
        this.pagina = pagina
        ;
    }

    static  TipoImpresion aleatorio(Random r){
        return values()[r.nextInt(values().length)];
    }
}

record Impresion(int idCliente, TipoImpresion tipo, long horaCreacion, BlockingQueue<TipoImpresion> buzonPersonal){}

public class ImprentaConcurrente {
    static void main() {

        ImprentaContext centro = new ImprentaContext();

        System.out.println("=== CENTRO LOGÍSTICO CONCURRENTE ===");
        System.out.println("Iniciando operaciones con " + centro.tecnicos + " tecnicos y "
                + centro.impresoras + " impresoras...\n");

        Thread[] hilosTecnicos = new Thread[centro.tecnicos];
        Thread[] hilosImpresoras = new Thread[centro.impresoras];

        // --- impresoras (consumidores) ---

        for (int i = 1; i <= centro.impresoras ; i++) {
            final int id = i;

            hilosImpresoras[i - 1] =  Thread.ofPlatform().name("impresora-" + id).start(() -> {
                try {
                    while (centro.centroAbierto || !centro.mostrador.isEmpty()){
                        Impresion impresion = centro.mostrador.take();
                        Thread.sleep(impresion.tipo().tiempoProcesamiento);

                        centro.impresionesCompletadas.incrementAndGet();
//                        centro.impresionesProcesadas.countDown();
                        System.out.println("[" + centro.ahora() + "] Robot-" + id + " procesa paquete "
                                + impresion.tipo() + " del Cliente-" + impresion.idCliente());
                        centro.totalIngresado.addAndGet(impresion.tipo().tarifaCent);
                        centro.documentosTerminados.put(impresion);
                    }
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }

            });
        }


        // --- Tecnicos (productores) ---

        for (int i = 1; i <= centro.tecnicos ; i++) {
            final int id = i;
            hilosTecnicos[i - 1] = Thread.ofPlatform().name("tecnico-" + id).start(() -> {
                try {
                    while (centro.centroAbierto || !centro.mostrador.isEmpty() || !centro.documentosTerminados.isEmpty()){
                        Impresion impresionLista = centro.documentosTerminados.poll();
                        if ( impresionLista != null){
                            Thread.sleep(centro.tiempoTecnicosMs);
                            System.out.println("[" + centro.ahora() + "] Tecnico-" + id
                                    + " sirve " + impresionLista.tipo() + " a Cliente-" + impresionLista.idCliente());
                            impresionLista.buzonPersonal().put(impresionLista.tipo());
                        } else{
                            Impresion impresion = centro.colaClientes.poll();

                            if (impresion != null){
                                Thread.sleep(centro.tiempoTecnicosMs);
                                System.out.println("[" + centro.ahora() + "] Tecnico-" + id
                                        + " registra paquete " + impresion.tipo());
                                if (centro.mostrador.remainingCapacity() == 0){
                                    centro.mostradorLLenoCount.incrementAndGet();
                                }
                                centro.mostrador.put(impresion);
                            }else {
                                Thread.sleep(1000);
                            }
                        }
                    }
                }catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // --- Clientes (hilos virtuales) ---
        Thread generadorClientes = Thread.ofPlatform().name("puerta-clientes").start(() -> {
            for (int i = 1; i <= centro.totalClientes ; i++) {
                final int id = i;

                Thread.ofVirtual().start(() -> {
                    System.out.println("[" + centro.ahora() + "] Cliente-" + String.format("%03d", id) + " llega a la puerta.");

                    try {
                        if (!centro.semaphoreMostrador.tryAcquire(2000, TimeUnit.MILLISECONDS)){
                            System.out.println("[" + centro.ahora() + "] Cliente-" + String.format("%03d", id) + " se va (se cansó de esperar).");
                            centro.impresionesNoAdmitidas.incrementAndGet();
                            return;
                        }

                        System.out.println("[" + centro.ahora() + "] Cliente-" + String.format("%03d", id) + " consigue mostrador.");
                        BlockingQueue<TipoImpresion> miBuzon = new ArrayBlockingQueue<>(1);
                        TipoImpresion tipo = TipoImpresion.aleatorio(new Random());

                        Thread.sleep(centro.llegadaClienteMs);
                        centro.colaClientes.put(new Impresion(id,tipo,System.currentTimeMillis(),miBuzon));

                        TipoImpresion impresionProcesada = miBuzon.take();
                        System.out.println("[" + centro.ahora() + "] Cliente-" + id + " recibe su impresion " + impresionProcesada);
                    }catch (InterruptedException e)  {
                        Thread.currentThread().interrupt();
                    } finally {
                        centro.semaphoreMostrador.release();
                        centro.impresionesProcesadas.countDown();
                    }

                });

            }
        });

        try {
            centro.impresionesProcesadas.await();
            generadorClientes.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        centro.centroAbierto = false;

        for (Thread i : hilosImpresoras) i.interrupt();
        for (Thread t : hilosTecnicos) t.interrupt();

        for (Thread i : hilosImpresoras) {
            try { i.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        for (Thread t : hilosTecnicos) {
            try { t.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        // --- ESTADÍSTICAS FINALES ---
        System.out.println("\n--- ESTADISTICAS FINALES ---");
        System.out.println("Paquetes procesados: " + centro.totalClientes + "/" + centro.totalClientes + " ✅");
        System.out.println("Cinta llena (veces): " + centro.mostradorLLenoCount.get());
        System.out.println("Dinero total ingresado: " + (centro.totalIngresado.get() / 100.0) + " €");

    }

}
