package Threads.Ejercicios.PracticaHilos.Ej05ParkingRefactorizado;

import java.util.Random;

public class Coche implements Runnable {
    private final String nombre;
    private final TipoVehiculo tipoVehiculo;
    private final Parking parking;

    public Coche(String nombre, TipoVehiculo tipoVehiculo, Parking parking) {
        this.nombre = nombre;
        this.tipoVehiculo = tipoVehiculo;
        this.parking = parking;
    }

    @Override
    public void run() {
        try {
            // Barrera de llegada inicial
            Thread.sleep(2000);

            // Intentar aparcar a través del gestor del parking
            Parking.ResultadoAparcamiento resultado = parking.intentarEntrar(nombre, tipoVehiculo);
            if (!resultado.isExito()) {
                return; // Si no hay sitio ni cola, se marcha rechazado
            }

            // Simular estancia (10 a 30 segundos aleatorios)
            int tiempoEstanciaSegundos = new Random().nextInt(21) + 10;
            Thread.sleep(tiempoEstanciaSegundos * 1000L);

            // Salir del parking y liquidar pago
            parking.registrarSalida(nombre, tipoVehiculo, resultado.isVip(), resultado.isNormal(), tiempoEstanciaSegundos);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}