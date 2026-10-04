package Threads.Ejercicios.PracticaHilos.Ej02Banco;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CuentaConSync implements CuentaBancaria{
    private double saldo;
    private List<String> historial = new ArrayList<>();
    private AtomicInteger operacionesExitosas = new AtomicInteger(0);
    private AtomicInteger operacionesFallidas = new AtomicInteger(0);

    public CuentaConSync(double saldo) {
        this.saldo = saldo;
    }

    @Override
    public AtomicInteger getOperacionesFallidas() {
        return operacionesFallidas;
    }

    public AtomicInteger getOperacionesExitosas() {
        return operacionesExitosas;
    }

    public CuentaConSync(AtomicInteger operacionesFallidas) {
        this.operacionesFallidas = operacionesFallidas;
    }

    public double getSaldo() {
        return saldo;
    }

    public synchronized boolean retirar(double cantidad){
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));
        if (saldo - cantidad < 0){
            historial.add("[" + timestamp + "]" + "Fondos insuficientes");
            operacionesFallidas.incrementAndGet();
            return false;
        }
        this.saldo = this.saldo - cantidad;
        operacionesExitosas.incrementAndGet();
        historial.add("[" + timestamp + "] Retiro exitoso: -" + cantidad + "€");
        return true;
    }

    public synchronized void ingresar(double cantidad){
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));
        historial.add("[" + timestamp + "] Ingreso exitoso: +" + cantidad + "€");
        this.saldo = saldo + cantidad;
        operacionesExitosas.incrementAndGet();
    }

    public synchronized double consultarSaldo(){
        return this.saldo;
    }

    public synchronized List<String> obtenerHistorial(){
        return this.historial;
    }
}
