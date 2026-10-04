package Threads.Ejercicios.PracticaHilos.Ej05ParkingAvanzado;

import java.util.Random;

public class Coche {
    private String name;
    private TipoVehiculo tipo;

    public   enum TipoVehiculo {
        NORMAL(1.0),
        VIP(2.0);
        private  final double dinero;
        TipoVehiculo(double dinero){this.dinero = dinero;};
        public double getDinero(){return dinero;};
    }

    public Coche(String s) {
        this.name = name;
        Random random = new Random();
        TipoVehiculo[] opciones = TipoVehiculo.values();
        this.tipo = opciones[random.nextInt(opciones.length)];
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    public String getName() {
        return name;
    }
}
