package Threads.Ejercicios.PracticaHilos.Ej02Banco;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CuentaVolatile implements CuentaBancaria{
    private volatile double saldo;
    private List<String> historial = new ArrayList<>();
    private AtomicInteger operacionesExitosas = new AtomicInteger(0);
    private AtomicInteger operacionesFallidas = new AtomicInteger(0);




    public CuentaVolatile(double saldo) {
        this.saldo = saldo;
    }

    public double getSaldo() {
        return saldo;
    }

    public  boolean retirar(double cantidad){
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));
        if (saldo - cantidad < 0){
            historial.add("[" + timestamp + "]" + "Fondos insuficientes");
            operacionesFallidas.decrementAndGet();
            return false;
        }
        this.saldo = this.saldo - cantidad;
        historial.add("[" + timestamp + "] Retiro exitoso: -" + cantidad + "€");
        operacionesExitosas.incrementAndGet();
        return true;
    }

    public void ingresar(double cantidad){
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));
        historial.add("[" + timestamp + "] Ingreso exitoso: +" + cantidad + "€");
        this.saldo = saldo + cantidad;
        operacionesExitosas.incrementAndGet();
    }

    public  double consultarSaldo(){
        return this.saldo;
    }

    public  List<String> obtenerHistorial(){
        return this.historial;
    }

    @Override
    public AtomicInteger getOperacionesExitosas() {
        return operacionesExitosas;
    }

    @Override
    public AtomicInteger getOperacionesFallidas() {
        return operacionesFallidas;
    }


}
