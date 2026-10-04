package Threads.Ejercicios.PracticaHilos.Ej05ParkingAvanzado;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Parking {

    // Enum provisto por el enunciado
    enum TipoVehiculo {
        NORMAL(1.0), // 1€/minuto
        VIP(2.0);    // 2€/minuto

        private final double tarifaPorMinuto;

        TipoVehiculo(double tarifaPorMinuto) {
            this.tarifaPorMinuto = tarifaPorMinuto;
        }

        public double getTarifaPorMinuto() {
            return tarifaPorMinuto;
        }
    }

    // Estadísticas globales y eventos seguros para multihilo
    private static final Queue<String> ultimosEventos = new LinkedList<>();
    private static final AtomicInteger vehiculosAtendidos = new AtomicInteger(0);
    private static final AtomicInteger vehiculosRechazados = new AtomicInteger(0);
    private static final AtomicLong ingresosCentimos = new AtomicLong(0);
    private static final AtomicLong sumaTiemposEstancia = new AtomicLong(0);
    private static final AtomicInteger maxOcupacion = new AtomicInteger(0);

    private static synchronized void registrarEvento(String mensaje) {
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String evento = "[" + hora + "] " + mensaje;
        if (ultimosEventos.size() >= 3) {
            ultimosEventos.poll();
        }
        ultimosEventos.add(evento);
    }

    private static synchronized void pintarPantalla(int normales, int vip, int cola) {
        // Limpiar pantalla y mover cursor arriba (ANSI)
        System.out.print("\033[H\033[2J");
        System.out.flush();

        String horaActual = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        double ingresosEuros = ingresosCentimos.get() / 100.0;

        System.out.println("=== PARKING INTELIGENTE ===");
        System.out.println("🅿️  Estado actual: [" + horaActual + "]");
        System.out.println("┌─────────────────────────────────┐");
        System.out.println("│ PLAZAS NORMALES: " + generarBarra(normales, 20) + " " + normales + "/20│");
        System.out.println("│ PLAZAS VIP:     " + generarBarra(vip, 5) + " " + vip + "/5 │");
        System.out.println("│ COLA DE ESPERA: " + generarBarraCola(cola, 10) + " " + cola + "/10│");
        System.out.printf("│ INGRESOS HOY:             %.2f€ │\n", ingresosEuros);
        System.out.println("└─────────────────────────────────┘");
        System.out.println("\nÚltimos eventos:");
        for (String ev : ultimosEventos) {
            System.out.println(ev);
        }
    }

    private static String generarBarra(int ocupadas, int total) {
        int bloques = (ocupadas * 10) / total;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(i < bloques ? "█" : "░");
        }
        return sb.toString();
    }

    private static String generarBarraCola(int ocupadas, int total) {
        int bloques = (ocupadas * 7) / total;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            sb.append(i < bloques ? "█" : "░");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws InterruptedException {
        final int plazasNormales = 20;
        final int plazasVIP = 5;
        Semaphore semaforoNormales = new Semaphore(plazasNormales, true);
        Semaphore semaforoVIP = new Semaphore(plazasVIP, true);
        AtomicInteger normalesOcupadas = new AtomicInteger(0);
        AtomicInteger vipOcupadas = new AtomicInteger(0);
        AtomicInteger colaEspera = new AtomicInteger(0);

        pintarPantalla(0, 0, 0);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 200; i++) {
                final int idCoche = i;
                // Asignamos tipo de vehículo (ej: 25% VIP, 75% Normales)
                TipoVehiculo tipo = (i % 4 == 0) ? TipoVehiculo.VIP : TipoVehiculo.NORMAL;
                String nombreCoche = "Coche-" + (idCoche < 10 ? "0" + idCoche : idCoche);

                executor.submit(() -> {
                    try {
                        // Barrera de entrada: 2 segundos por coche
                        Thread.sleep(2000);

                        boolean aparcadoVIP = false;
                        boolean aparcadoNormal = false;

                        if (tipo == TipoVehiculo.VIP) {
                            if (semaforoVIP.tryAcquire()) {
                                vipOcupadas.incrementAndGet();
                                aparcadoVIP = true;
                                registrarEvento("⭐ " + nombreCoche + " (VIP) entra - Plaza VIP");
                            } else if (semaforoNormales.tryAcquire()) {
                                normalesOcupadas.incrementAndGet();
                                aparcadoNormal = true;
                                registrarEvento("⭐ " + nombreCoche + " (VIP) entra - Plaza Normal");
                            } else if (colaEspera.get() < 10) {
                                colaEspera.incrementAndGet();
                                registrarEvento("⏳ " + nombreCoche + " (VIP) esperando en cola");
                                boolean aparcadoEnCola = false;

                                while (!aparcadoEnCola) {
                                    if (semaforoVIP.tryAcquire()) {
                                        vipOcupadas.incrementAndGet();
                                        aparcadoVIP = true;
                                        aparcadoEnCola = true;
                                        registrarEvento("⭐ " + nombreCoche + " (VIP) sale de cola - Plaza VIP");
                                    } else if (semaforoNormales.tryAcquire()) {
                                        normalesOcupadas.incrementAndGet();
                                        aparcadoNormal = true;
                                        aparcadoEnCola = true;
                                        registrarEvento("⭐ " + nombreCoche + " (VIP) sale de cola - Plaza Normal");
                                    } else {
                                        Thread.sleep(200);
                                    }
                                }
                                colaEspera.decrementAndGet();
                            } else {
                                vehiculosRechazados.incrementAndGet();
                                registrarEvento("❌ " + nombreCoche + " (VIP) se marcha (lleno)");
                                return;
                            }
                        } else {
                            // Coche NORMAL
                            if (semaforoNormales.tryAcquire()) {
                                normalesOcupadas.incrementAndGet();
                                aparcadoNormal = true;
                                registrarEvento("🚗 " + nombreCoche + " (NORMAL) entra - Plaza Normal");
                            } else if (colaEspera.get() < 10) {
                                colaEspera.incrementAndGet();
                                registrarEvento("🚗 " + nombreCoche + " (NORMAL) esperando en cola");
                                boolean aparcadoEnCola = false;

                                while (!aparcadoEnCola) {
                                    if (semaforoNormales.tryAcquire()) {
                                        normalesOcupadas.incrementAndGet();
                                        aparcadoNormal = true;
                                        aparcadoEnCola = true;
                                        registrarEvento("🚗 " + nombreCoche + " (NORMAL) sale de cola - Plaza Normal");
                                    } else {
                                        Thread.sleep(200);
                                    }
                                }
                                colaEspera.decrementAndGet();
                            } else {
                                vehiculosRechazados.incrementAndGet();
                                registrarEvento("❌ " + nombreCoche + " (NORMAL) se marcha (lleno)");
                                return;
                            }
                        }

                        // Vehículo atendido con éxito
                        vehiculosAtendidos.incrementAndGet();

                        // Control de ocupación máxima total
                        int totalActual = normalesOcupadas.get() + vipOcupadas.get();
                        maxOcupacion.updateAndGet(max -> Math.max(max, totalActual));

                        pintarPantalla(normalesOcupadas.get(), vipOcupadas.get(), colaEspera.get());

                        // --- ESTANCIA (10 a 30 segundos aleatorios) ---
                        int tiempoEstanciaSegundos = new Random().nextInt(21) + 10;
                        sumaTiemposEstancia.addAndGet(tiempoEstanciaSegundos);
                        Thread.sleep(tiempoEstanciaSegundos * 1000L);

                        // --- CÁLCULO DE TARIFA ---
                        // Tarifa = (Segundos / 60.0) * tarifaPorMinuto
                        double pagoEuros = (tiempoEstanciaSegundos / 60.0) * tipo.getTarifaPorMinuto();
                        long pagoCentimos = Math.round(pagoEuros * 100);
                        ingresosCentimos.addAndGet(pagoCentimos);

                        // --- SALIDA ---
                        if (aparcadoVIP) {
                            semaforoVIP.release();
                            vipOcupadas.decrementAndGet();
                            registrarEvento("⭐ " + nombreCoche + " (VIP) sale - Pagó: " + String.format("%.2f€", pagoEuros));
                        } else if (aparcadoNormal) {
                            semaforoNormales.release();
                            normalesOcupadas.decrementAndGet();
                            registrarEvento("🚗 " + nombreCoche + " (NORMAL) sale - Pagó: " + String.format("%.2f€", pagoEuros));
                        }

                        pintarPantalla(normalesOcupadas.get(), vipOcupadas.get(), colaEspera.get());

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                Thread.sleep(500);
            }
        } // El try-with-resources espera a que terminen todos los 200 hilos

        // --- ESTADÍSTICAS FINALES ---
        int totalProcesados = 200;
        int atendidos = vehiculosAtendidos.get();
        int rechazados = vehiculosRechazados.get();
        double porcentajeAtendidos = (atendidos * 100.0) / totalProcesados;
        double tiempoPromedio = atendidos > 0 ? (double) sumaTiemposEstancia.get() / atendidos : 0;
        double ingresosTotalesEuros = ingresosCentimos.get() / 100.0;

        System.out.println("\n\n--- RESUMEN DEL DÍA ---");
        System.out.println("Vehículos procesados: " + totalProcesados);
        System.out.printf("Vehículos atendidos: %d (%.1f%%)\n", atendidos, porcentajeAtendidos);
        System.out.println("Vehículos rechazados: " + rechazados + " (parking+cola llenos)");
        System.out.printf("Tiempo promedio de estancia: %.1fs\n", tiempoPromedio);
        System.out.printf("Ingresos totales: %.2f€\n", ingresosTotalesEuros);
        System.out.println("Ocupación máxima: " + maxOcupacion.get() + "/25 plazas (100%)");
    }
}