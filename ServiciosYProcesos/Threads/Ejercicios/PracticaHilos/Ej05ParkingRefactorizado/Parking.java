package Threads.Ejercicios.PracticaHilos.Ej05ParkingRefactorizado;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Parking {
    private final Semaphore semaforoNormales = new Semaphore(20, true);
    private final Semaphore semaforoVIP = new Semaphore(5, true);

    private final AtomicInteger normalesOcupadas = new AtomicInteger(0);
    private final AtomicInteger vipOcupadas = new AtomicInteger(0);
    private final AtomicInteger colaEspera = new AtomicInteger(0);

    // Estadísticas y eventos seguros para hilos
    private final Queue<String> ultimosEventos = new LinkedList<>();
    private final AtomicInteger vehiculosAtendidos = new AtomicInteger(0);
    private final AtomicInteger vehiculosRechazados = new AtomicInteger(0);
    private final AtomicLong ingresosCentimos = new AtomicLong(0);
    private final AtomicLong sumaTiemposEstancia = new AtomicLong(0);
    private final AtomicInteger maxOcupacion = new AtomicInteger(0);

    public synchronized void registrarEvento(String mensaje) {
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String evento = "[" + hora + "] " + mensaje;
        if (ultimosEventos.size() >= 3) {
            ultimosEventos.poll();
        }
        ultimosEventos.add(evento);
    }

    public synchronized void pintarPantalla() {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        String horaActual = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        double ingresosEuros = ingresosCentimos.get() / 100.0;

        System.out.println("=== PARKING INTELIGENTE (REFACTORIZADO) ===");
        System.out.println("🅿️  Estado actual: [" + horaActual + "]");
        System.out.println("┌─────────────────────────────────┐");
        System.out.println("│ PLAZAS NORMALES: " + generarBarra(normalesOcupadas.get(), 20) + " " + normalesOcupadas.get() + "/20│");
        System.out.println("│ PLAZAS VIP:     " + generarBarra(vipOcupadas.get(), 5) + " " + vipOcupadas.get() + "/5 │");
        System.out.println("│ COLA DE ESPERA: " + generarBarraCola(colaEspera.get(), 10) + " " + colaEspera.get() + "/10│");
        System.out.printf("│ INGRESOS HOY:             %.2f€ │\n", ingresosEuros);
        System.out.println("└─────────────────────────────────┘");
        System.out.println("\nÚltimos eventos:");
        for (String ev : ultimosEventos) {
            System.out.println(ev);
        }
    }

    private String generarBarra(int ocupadas, int total) {
        int bloques = (ocupadas * 10) / total;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(i < bloques ? "█" : "░");
        }
        return sb.toString();
    }

    private String generarBarraCola(int ocupadas, int total) {
        int bloques = (ocupadas * 7) / total;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            sb.append(i < bloques ? "█" : "░");
        }
        return sb.toString();
    }

    // Lógica para intentar entrar al parking
    public ResultadoAparcamiento intentarEntrar(String nombreCoche, TipoVehiculo tipo) throws InterruptedException {
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
                return new ResultadoAparcamiento(false, false);
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
                return new ResultadoAparcamiento(false, false);
            }
        }

        vehiculosAtendidos.incrementAndGet();
        int totalActual = normalesOcupadas.get() + vipOcupadas.get();
        maxOcupacion.updateAndGet(max -> Math.max(max, totalActual));
        pintarPantalla();

        return new ResultadoAparcamiento(aparcadoVIP, aparcadoNormal);
    }

    // Lógica para registrar salida y liberar recursos
    public void registrarSalida(String nombreCoche, TipoVehiculo tipo, boolean vip, boolean normal, int tiempoEstanciaSegundos) {
        double pagoEuros = (tiempoEstanciaSegundos / 60.0) * tipo.getTarifaPorMinuto();
        long pagoCentimos = Math.round(pagoEuros * 100);
        ingresosCentimos.addAndGet(pagoCentimos);
        sumaTiemposEstancia.addAndGet(tiempoEstanciaSegundos);

        if (vip) {
            semaforoVIP.release();
            vipOcupadas.decrementAndGet();
            registrarEvento("⭐ " + nombreCoche + " (VIP) sale - Pagó: " + String.format("%.2f€", pagoEuros));
        } else if (normal) {
            semaforoNormales.release();
            normalesOcupadas.decrementAndGet();
            registrarEvento("🚗 " + nombreCoche + " (NORMAL) sale - Pagó: " + String.format("%.2f€", pagoEuros));
        }

        pintarPantalla();
    }

    // Getters para el resumen final
    public int getAtendidos() { return vehiculosAtendidos.get(); }
    public int getRechazados() { return vehiculosRechazados.get(); }
    public double getIngresosTotales() { return ingresosCentimos.get() / 100.0; }
    public double getTiempoPromedio() {
        int atendidos = vehiculosAtendidos.get();
        return atendidos > 0 ? (double) sumaTiemposEstancia.get() / atendidos : 0;
    }
    public int getMaxOcupacion() { return maxOcupacion.get(); }

    // Clase auxiliar interna para devolver el resultado del aparcamiento
    public static class ResultadoAparcamiento {
        private final boolean vip;
        private final boolean normal;
        private final boolean exito;

        public ResultadoAparcamiento(boolean vip, boolean normal) {
            this.vip = vip;
            this.normal = normal;
            this.exito = vip || normal;
        }

        public boolean isVip() { return vip; }
        public boolean isNormal() { return normal; }
        public boolean isExito() { return exito; }
    }
}