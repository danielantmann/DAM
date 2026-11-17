package Threads.examen;
/*
 PROBLEMA 3 - El Restaurante Concurrente de clase dado como solucion
 Patron Productor-Consumidor con 5 camareros (productores), 3 cocineros
 (consumidores) y 100 clientes (hilos virtuales).
 con los siguientes cambios
 1Pto El cierre del resturante deberá de ser más controlado, y no salir hasta parar a los cocineros y camareros.
 0,5 Pto  Tb pon precio a los platos y acumula cuanto ha ganado el restaurente en el servicio.
 2Pto Flujo de un cliente (hilo virtual):
   llega -> espera mesa libre durante un tiempo(elige el tiempo para que no se vayan todos) -> se sienta si la consigue y si no se va-> pide -> espera el plato
         -> se lo come -> libera la mesa -> se va
 2Pto Los camareros tienen PRIORIDAD para servir platos: antes de tomar un pedido nuevo
     o de meter uno en cocina, siempre miran si hay platos listos para entregarlos a los clientes.
 2Pto Cambia los clientes por un grupo de clientes.  Flujo de un grupo:
       la primera parte hasta que se sientan es igual que un cliente solo -> el camarero toma la comanda (un plato por persona)
             -> espera a que le sirvan TODOS los platos -> comen -> liberan la mesa -> se van

*/
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Ejercicio3SolucionPasoaPaso {

    enum TipoPlato {
        ENSALADA(2000,850),
        PASTA(3000,1200),
        PIZZA(4000,100),
        CARNE(5000,800);
         final int tiempoMs;
         final int precioCentimos;
        TipoPlato(int tiempoMs  , int precioCentimos) {
            this.tiempoMs = tiempoMs;
            this.precioCentimos = precioCentimos;
        }
        static TipoPlato aleatorio(Random r) {
            return values()[r.nextInt(values().length)];
        }
    }

    record Pedido(int cliente, TipoPlato plato, long horaCreacionMs, BlockingQueue<TipoPlato> buzonesCliente) {}

    // --- Constantes del enunciado ---
    static final int COCINEROS = 3;
    static final int CAMAREROS = 5;
    static final int CLIENTES = 100;
    static final int CAPACIDAD_MESA = 10;
    static final long LLEGADA_CLIENTE_MS = 500;
    static final long TIEMPO_CAMARERO_MS = 1000;

    // --- Recursos compartidos ---
    static final BlockingQueue<Pedido> mesa = new ArrayBlockingQueue<>(CAPACIDAD_MESA);
    static final CountDownLatch clientesAtendidos = new CountDownLatch(CLIENTES);
    static final BlockingQueue<Pedido> platosListos = new ArrayBlockingQueue<>(20);
    // Cola intermedia clientes -> camareros (ilimitada: los clientes nunca se quedan sin atender)
    static final BlockingQueue<Pedido> colaClientes = new ArrayBlockingQueue<>(CLIENTES);
    static final Semaphore mesasRestaurante = new Semaphore(CAPACIDAD_MESA, true);
    // --- Estadisticas ---
    static final AtomicInteger platosServidos = new AtomicInteger();
    static final AtomicLong esperaTotalMs = new AtomicLong();
    static final AtomicInteger mesaLlenaCount = new AtomicInteger();
    static final AtomicLong cocinerosEsperandoMs = new AtomicLong();
    static final AtomicLong colaMesaLLena = new AtomicLong();
    static final AtomicLong totalRecaudado = new AtomicLong(0);
    static volatile boolean restauranteAbierto = true;
    static String ahora() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== RESTAURANTE CONCURRENTE ===");
        System.out.println("Iniciando servicio con " + COCINEROS + " cocineros y "
                + CAMAREROS + " camareros...\n");

        Thread[] hilosCocineros = new Thread[COCINEROS];
        Thread[] hilosCamareros = new Thread[CAMAREROS];

        // --- Cocineros (consumidores) ---
        for (int i = 1; i <= COCINEROS; i++) {
            final int id = i;
            hilosCocineros[i-1] = Thread.ofPlatform().name("cocinero-" + id).start(() -> {
                try {
                    while (restauranteAbierto || !mesa.isEmpty()) {
                        long t0 = System.currentTimeMillis();
                        Pedido p = mesa.take(); // espera si no hay pedidos
                        cocinerosEsperandoMs.addAndGet(System.currentTimeMillis() - t0);

                        Thread.sleep(p.plato().tiempoMs); // cocina el plato
                        esperaTotalMs.addAndGet(System.currentTimeMillis() - p.horaCreacionMs());
                        platosServidos.incrementAndGet();


                        System.out.println("[" + ahora() + "] Cocinero-" + id + " termina "
                                + p.plato() + " para Cliente-" + p.cliente());
                        totalRecaudado.addAndGet(p.plato.precioCentimos);

                        platosListos.put(p);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // cierre del restaurante
                }
            });
        }

        // --- Camareros (productores) ---
        // --- Camareros (productores y repartidores con prioridad) ---
        for (int i = 1; i <= CAMAREROS; i++) {
            final int id = i;
            hilosCamareros[i-1] = Thread.ofPlatform().name("camarero-" + id).start(() -> {
                try {
                    while (restauranteAbierto || !mesa.isEmpty() || !platosListos.isEmpty()) {

                        // 1. PRIORIDAD: ¿Hay platos listos en el mostrador?
                        Pedido platoListo = platosListos.poll(); // .poll() no bloquea, devuelve null si está vacío

                        if (platoListo != null) {
                            Thread.sleep(TIEMPO_CAMARERO_MS); // Tiempo que tarda el camarero en llevar el plato
                            System.out.println("[" + ahora() + "] Camarero-" + id
                                    + " sirve " + platoListo.plato() + " a Cliente-" + platoListo.cliente());

                            // ¡AQUÍ ES DONDE SE ENTREGA AL CLIENTE!
                            // El camarero deposita el plato en el buzón personal del cliente
                            platoListo.buzonesCliente().put(platoListo.plato());

                        } else {
                            // 2. SI NO HAY PLATOS LISTOS: Atendemos un pedido de la cola de clientes
                            Pedido p = colaClientes.poll(); // Usamos poll() también para no bloquear si no hay clientes

                            if (p != null) {
                                Thread.sleep(TIEMPO_CAMARERO_MS); // Tiempo para tomar el pedido
                                System.out.println("[" + ahora() + "] Camarero-" + id
                                        + " toma pedido " + p.plato() + " de Cliente-" + p.cliente());

                                if (mesa.remainingCapacity() == 0) {
                                    mesaLlenaCount.incrementAndGet();
                                }
                                mesa.put(p); // Lo mete en la cocina
                            } else {
                                // Breve respiro si en ese preciso instante no hay platos listos ni clientes
                                Thread.sleep(100);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // --- Clientes (hilos virtuales, llegan cada 500 ms) ---
// --- Clientes (hilos virtuales, llegan cada 500 ms) ---
        Thread generadorClientes = Thread.ofPlatform().name("puerta").start(() -> {
            for (int i = 1; i <= CLIENTES; i++) {
                final int id = i;

                // Cada cliente es un hilo virtual independiente
                Thread.ofVirtual().start(() -> {
                    try {
                        System.out.println("[" + ahora() + "] Cliente-" + String.format("%03d", id) + " llega a la puerta.");

                        // 1. Espera mesa libre durante un tiempo (ej: 2 segundos)
                        if (!mesasRestaurante.tryAcquire(2000, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                            System.out.println("[" + ahora() + "] Cliente-" + String.format("%03d", id) + " se va sin cenar (se cansó de esperar mesa).");
                            colaMesaLLena.incrementAndGet();
                            return; // El cliente se marcha
                        }

                        // 2. Se sienta y pide
                        System.out.println("[" + ahora() + "] Cliente-" + String.format("%03d", id) + " se sienta en una mesa.");
                        TipoPlato plato = TipoPlato.aleatorio(new Random());
                        System.out.println("[" + ahora() + "] Cliente-" + String.format("%03d", id) + " pide " + plato);

                        BlockingQueue<TipoPlato> miBuzon = new ArrayBlockingQueue<>(1);

                        colaClientes.put(new Pedido(id, plato, System.currentTimeMillis(), miBuzon));

                        // El cliente espera a que el cocinero le entregue su plato en el buzón
                        TipoPlato platoServido = miBuzon.take();
                        System.out.println("[" + ahora() + "] Cliente-" + id + " recibe y se come su " + platoServido);

                        // Simulamos el tiempo que tarda en comerse el plato (opcional, o directo al finally)
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }finally {
                        mesasRestaurante.release();
                        clientesAtendidos.countDown();
                    }
                });

                // 3. El generador espera 500ms para que llegue el siguiente cliente a la puerta
                try {
                    Thread.sleep(LLEGADA_CLIENTE_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });


        clientesAtendidos.await(); // el restaurante no cierra hasta atender a los 100
        generadorClientes.join();

        // 1. Damos la orden de cierre
        restauranteAbierto = false;

        // 2. ¡MUY IMPORTANTE! Despertamos a los hilos que estén dormidos en el take()
        for (Thread c : hilosCocineros) c.interrupt();
        for (Thread cam : hilosCamareros) cam.interrupt();

        // 3. Ahora sí, esperamos a que recojan y cierren ordenadamente

        for (Thread camarero : hilosCamareros) {
            try { camarero.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        for (Thread cocinero : hilosCocineros) {
            try { cocinero.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        System.out.println("\n--- ESTADISTICAS FINALES ---");
        System.out.println("Clientes atendidos: " + CLIENTES + "/" + CLIENTES + " ✅");
        System.out.println("Platos servidos: " + platosServidos.get());
        System.out.println("Tiempo promedio de espera: "
                + (esperaTotalMs.get() / 1000.0 / CLIENTES) + "s");
        System.out.println("Mesa llena (veces): " + mesaLlenaCount.get());
        System.out.println("Cocineros esperando (tiempo acumulado): "
                + (cocinerosEsperandoMs.get() / 1000) + "s total");
        System.out.println("Eficiencia: el sistema soporta " + CAMAREROS + " pedidos/s en entrada"
                + " y " + COCINEROS + " platos en paralelo en cocina.");
        System.out.println("Recaudación total: " + (totalRecaudado.get() / 100.0) + " €");
        //System.exit(0); // cerrar los hilos camareros/cocineros (bucle infinito por disenyo)
    }

}
