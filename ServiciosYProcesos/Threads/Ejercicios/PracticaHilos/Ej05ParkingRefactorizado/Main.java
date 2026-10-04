package Threads.Ejercicios.PracticaHilos.Ej05ParkingRefactorizado;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Parking parking = new Parking();
        parking.pintarPantalla();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 200; i++) {
                final int idCoche = i;
                TipoVehiculo tipo = (i % 4 == 0) ? TipoVehiculo.VIP : TipoVehiculo.NORMAL;
                String nombreCoche = "Coche-" + (idCoche < 10 ? "0" + idCoche : idCoche);

                executor.submit(new Coche(nombreCoche, tipo, parking));

                Thread.sleep(500);
            }
        }


        System.out.println("\n\n--- RESUMEN DEL DÍA ---");
        System.out.println("Vehículos procesados: 200");
        System.out.printf("Vehículos atendidos: %d (%.1f%%)\n", parking.getAtendidos(), (parking.getAtendidos() * 100.0) / 200);
        System.out.println("Vehículos rechazados: " + parking.getRechazados() + " (parking+cola llenos)");
        System.out.printf("Tiempo promedio de estancia: %.1fs\n", parking.getTiempoPromedio());
        System.out.printf("Ingresos totales: %.2f€\n", parking.getIngresosTotales());
        System.out.println("Ocupación máxima: " + parking.getMaxOcupacion() + "/25 plazas (100%)");
    }
}