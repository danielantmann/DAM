package Threads.Ejercicios.PracticaExamen;
//Ejercicio Examen B: El Cajero Automático y el ReentrantLock con tryLock
//Enunciado:
//Modifica la lógica de transferencia que vimos antes utilizando ReentrantLock en lugar de synchronized.
//
//Crea una clase Cuenta con un atributo id y saldo.
//
//Implementa un método transferir(Cuenta destino, double cantidad) que intente bloquear ambas cuentas para realizar la transferencia de forma segura.
//
//Requisito clave: En lugar de bloquearse ciegamente (lo que causaría un deadlock), el método debe utilizar tryLock(). Si no puede adquirir el bloqueo de la segunda cuenta en un tiempo determinado (o de forma inmediata), debe liberar la primera cuenta, esperar un breve instante e reintentar la operación (evitando el bloqueo mutuo por diseño).
//
//Crea dos hilos que intenten transferir dinero mutuamente entre la Cuenta 1 y la Cuenta 2 de forma simultánea.

import java.util.concurrent.locks.ReentrantLock;

public class Cuenta {
    private int id;
    private double saldo;
    // Cada cuenta tiene SU PROPIO candado (no static)
    private final ReentrantLock lock = new ReentrantLock();

    public Cuenta(int id, double saldo) {
        this.id = id;
        this.saldo = saldo;
    }

    public void transferir(Cuenta destino, double cantidad) {
        // Bucle para reintentar si no conseguimos ambos bloqueos
        while (true) {
            try {
                // Intentamos bloquear la cuenta origen (this)
                if (this.lock.tryLock()) {
                    try {
                        // Intentamos bloquear la cuenta destino (usando tryLock con tiempo o inmediato)
                        if (destino.lock.tryLock()) {
                            try {
                                // ¡ÉXITO! Tenemos ambos bloqueos. Hacemos la transferencia de forma segura.
                                if (this.saldo >= cantidad) {
                                    this.saldo -= cantidad;
                                    destino.saldo += cantidad;
                                    System.out.println("Transferencia de " + cantidad + " de Cuenta " + this.id + " a Cuenta " + destino.id + " exitosa.");
                                } else {
                                    System.out.println("Fondos insuficientes en Cuenta " + this.id);
                                }
                                return; // Salimos del método porque ya terminó con éxito
                            } finally {
                                destino.lock.unlock(); // Liberamos el destino
                            }
                        }
                    } finally {
                        this.lock.unlock(); // Liberamos el origen si no pudimos conseguir el destino
                    }
                }

                // Si no pudimos conseguir ambos, esperamos un breve instante antes de reintentar
                Thread.sleep(50);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Transferencia interrumpida.");
                return;
            }
        }
    }

    static void main() {
        Cuenta cuenta1 = new Cuenta(1, 1000.0);
        Cuenta cuenta2 = new Cuenta(2, 1000.0);

        // Hilo 1: Cuenta 1 transfiere a Cuenta 2
        Thread hilo1 = new Thread(() -> {
            cuenta1.transferir(cuenta2, 200.0);
        });

        // Hilo 2: Cuenta 2 transfiere a Cuenta 1 (¡Aquí es donde se cruzarían y harían deadlock si no fuera por el tryLock!)
        Thread hilo2 = new Thread(() -> {
            cuenta2.transferir(cuenta1, 150.0);
        });

        hilo1.start();
        hilo2.start();
    }
}
