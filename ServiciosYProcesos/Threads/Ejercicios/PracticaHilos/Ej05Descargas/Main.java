package Threads.Ejercicios.PracticaHilos.Ej05Descargas;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    // Record interno solo para envolver la tarea en la cola (ordena los Premium primero)
    record TareaDescarga(Usuario usuario, TipoArchivo archivo, CompletableFuture<Boolean> respuesta)
            implements Comparable<TareaDescarga> {
        @Override
        public int compareTo(TareaDescarga o) {
            // Premium (true) va antes que Normal (false)
            return Boolean.compare(!this.usuario.isPremium(), !o.usuario.isPremium());
        }
    }

    // --- ESTADO COMPARTIDO DE LA PANTALLA ---
    private static final Queue<String> ultimosEventos = new LinkedList<>();
    private static final ConcurrentHashMap<String, String> descargasActivasMap = new ConcurrentHashMap<>();
    private static final AtomicInteger colaEsperaContador = new AtomicInteger(0);
    private static final PriorityBlockingQueue<TareaDescarga> colaServidor = new PriorityBlockingQueue<>();

    // --- MÉTODOS AUXILIARES DE CONSOLA ---
    private static synchronized void registrarEvento(String mensaje) {
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        if (ultimosEventos.size() >= 3) ultimosEventos.poll();
        ultimosEventos.add("[" + hora + "] " + mensaje);
    }

    private static String generarBarra(int porcentaje) {
        int bloques = porcentaje / 10;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) sb.append(i < bloques ? "█" : "░");
        return sb.toString();
    }

    private static synchronized void pintarPantalla(int conexionesActivas, int completadas, int fallidas) {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        System.out.println("=== GESTOR DE DESCARGAS ===");
        System.out.println("🖥️ Servidor: " + conexionesActivas + "/5 conexiones activas");
        System.out.println("\n🔄 Descargas activas:");
        System.out.println("┌──────────────────────────────────────────┐");

        if (descargasActivasMap.isEmpty()) {
            System.out.println("│          (Sin descargas activas)         │");
        } else {
            for (String infoDescarga : descargasActivasMap.values()) {
                System.out.printf("│ %-40s │\n", infoDescarga);
            }
        }
        System.out.println("└──────────────────────────────────────────┘");
        System.out.println("⏳ Cola de espera: " + colaEsperaContador.get() + " usuarios");
        System.out.println("❌ Fallos: " + fallidas + " | ✅ Completadas: " + completadas);

        System.out.println("\nÚltimos eventos:");
        for (String ev : ultimosEventos) {
            System.out.println(ev);
        }
    }

    // --- BUCLE PRINCIPAL ---
    public static void main(String[] args) {
        final int LIMITE = 50;
        final int INTENTOS = 3;
        final long ESCALA = 2L;
        final double PROB_FALLO = 0.20;

        Random random = new Random();
        AtomicInteger descargasFinalizadas = new AtomicInteger(0);
        AtomicInteger descargasFallidas = new AtomicInteger(0);
        AtomicInteger conexionesActivas = new AtomicInteger(0);

        TipoArchivo[] listaArchivos = TipoArchivo.values();
        CompletableFuture<?>[] arrayDescargas = new CompletableFuture[LIMITE];

        // 1. REFRESCADOR DE PANTALLA (Ejecuta pintarPantalla cada 200 ms)
        ScheduledExecutorService relojPantalla = Executors.newSingleThreadScheduledExecutor();
        relojPantalla.scheduleAtFixedRate(() -> {
            pintarPantalla(conexionesActivas.get(), descargasFinalizadas.get(), descargasFallidas.get());
        }, 0, 200, TimeUnit.MILLISECONDS);

        // 2. POOL DEL SERVIDOR (5 hilos atendiendo la cola ordenada)
        ExecutorService servidorPool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            servidorPool.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        TareaDescarga tarea = colaServidor.take(); // Prioriza usuarios Premium

                        colaEsperaContador.decrementAndGet();
                        conexionesActivas.incrementAndGet();

                        // Simulación del progreso de descarga
                        String tag = tarea.usuario().isPremium() ? "★ " : "  ";
                        for (int p = 1; p <= 4; p++) {
                            Thread.sleep((tarea.archivo().getSizeMB() * ESCALA) / 4);
                            int porc = p * 25;
                            String descInfo = tag + tarea.usuario().getNombre() + " │ "
                                    + String.format("%-10s", tarea.archivo().name()) + " "
                                    + generarBarra(porc) + " " + porc + "%";

                            descargasActivasMap.put(tarea.usuario().getNombre(), descInfo);
                        }

                        boolean exito = random.nextDouble() >= PROB_FALLO;

                        descargasActivasMap.remove(tarea.usuario().getNombre());
                        conexionesActivas.decrementAndGet();

                        // Resuelve el ticket del cliente
                        tarea.respuesta().complete(exito);

                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
        }

        // 3. GENERADOR DE CLIENTES (Hilos Virtuales)
        try (ExecutorService clientes = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 1; i <= LIMITE; i++) {
                final int idUsuario = i;
                boolean esPremium = random.nextDouble() < 0.30; // 30% usuarios Premium
                Usuario usuario = new Usuario("ID-" + (idUsuario < 10 ? "0" + idUsuario : idUsuario), esPremium);
                TipoArchivo archivo = listaArchivos[random.nextInt(listaArchivos.length)];

                arrayDescargas[i - 1] = CompletableFuture.runAsync(() -> {
                    boolean conseguida = false;

                    for (int j = 1; j <= INTENTOS && !conseguida; j++) {
                        colaEsperaContador.incrementAndGet();
                        registrarEvento(usuario.getNombre() + " esperando en cola (intento " + j + ")");

                        CompletableFuture<Boolean> miTicket = new CompletableFuture<>();
                        colaServidor.put(new TareaDescarga(usuario, archivo, miTicket));

                        // Bloquea solo al hilo virtual del cliente hasta que el servidor responde
                        boolean exito = miTicket.join();

                        if (exito) {
                            registrarEvento("✅ " + usuario.getNombre() + " completó: " + archivo.name());
                            descargasFinalizadas.incrementAndGet();
                            conseguida = true;
                        } else if (j < INTENTOS) {
                            registrarEvento("⚠️ " + usuario.getNombre() + " error de red. Reintentando...");
                        }
                    }

                    if (!conseguida) {
                        registrarEvento("❌ " + usuario.getNombre() + " agotó sus " + INTENTOS + " intentos.");
                        descargasFallidas.incrementAndGet();
                    }
                }, clientes);

                try {
                    Thread.sleep(80); // Escalona la llegada de clientes
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Esperar a que terminen los 50 clientes
            CompletableFuture.allOf(arrayDescargas).join();
        }

        // 4. LIMPIEZA FINAL
        servidorPool.shutdownNow();
        relojPantalla.shutdown();

        // Renderizado final estático
        pintarPantalla(0, descargasFinalizadas.get(), descargasFallidas.get());
        System.out.println("\n=== ESTADÍSTICAS DEL SERVIDOR ===");
        System.out.println("Archivos descargados con éxito: " + descargasFinalizadas.get() + "/" + LIMITE + " solicitudes");
        System.out.println("Descargas fallidas (reintentos agotados): " + descargasFallidas.get());
    }
}