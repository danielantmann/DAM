package Threads.Ejercicios.PracticaHilos.Ej02Banco;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface CuentaBancaria {
    boolean retirar(double cantidad);
    void ingresar(double cantidad);
    double consultarSaldo();
    List<String> obtenerHistorial();
    AtomicInteger getOperacionesExitosas();
    AtomicInteger getOperacionesFallidas();
}
