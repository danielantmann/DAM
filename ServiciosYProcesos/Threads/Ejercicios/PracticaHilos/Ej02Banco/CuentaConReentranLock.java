package Threads.Ejercicios.PracticaHilos.Ej02Banco;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class CuentaConReentranLock implements CuentaBancaria{
    private double saldo;
    private List<String> historial = new ArrayList<>();
    private final ReentrantLock candado = new ReentrantLock();
    private AtomicInteger operacionesExitosas = new AtomicInteger(0);
    private AtomicInteger operacionesFallidas = new AtomicInteger(0);

    public CuentaConReentranLock(double saldo) {
        this.saldo = saldo;
    }

    public AtomicInteger getOperacionesExitosas() {
        return operacionesExitosas;
    }

    public AtomicInteger getOperacionesFallidas() {
        return operacionesFallidas;
    }

    public boolean retirar(double cantidad){
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));
        candado.lock();
        try{
            if (saldo - cantidad < 0){
                historial.add("[" + timestamp + "]" + "Fondos insuficientes");
                operacionesFallidas.incrementAndGet();
                return false;
            }
            this.saldo = this.saldo - cantidad;
            historial.add("[" + timestamp + "] Retiro exitoso: -" + cantidad + "€");
            operacionesExitosas.incrementAndGet();
            return true;
        }finally {
            candado.unlock();
        }
    }

    public void ingresar(double cantidad){
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));
        candado.lock();
        try{
            this.saldo = saldo + cantidad;
            operacionesExitosas.incrementAndGet();
            historial.add("[" + timestamp + "] Ingreso exitoso: +" + cantidad + "€");
        }finally {
            candado.unlock();
        }
    }

    public double consultarSaldo(){
        candado.lock();
        try{
            return this.saldo;
        }finally {
            candado.unlock();
        }
    }

    public List<String> obtenerHistorial(){
        candado.lock();
        try {
            return this.historial;
        }finally {
            candado.unlock();
        }
    }

}
