package Threads.Ejercicios.SimulacroExam;

//Sistema bancario concurrente — 55 minutos (5 puntos)
//Implementa una clase CuentaBancaria con un saldo inicial de 1.000 €.
//Crea diez clientes que realicen cinco operaciones cada uno.
//Cada operación debe ser una retirada de 50 € o un ingreso de 20 €, según el tipo asignado al cliente.
//Requisitos:
//        - Todos los clientes trabajan simultáneamente.
//- Utiliza ReentrantLock o synchronized para proteger el saldo.
//        - Una retirada no puede realizarse si no hay suficiente dinero.
//        - Registra las operaciones aceptadas y rechazadas.
//        - Cada registro debe indicar el cliente, la operación y el saldo resultante.
//- El hilo principal debe esperar a todos los clientes antes de mostrar el resumen.
//        Salida esperada, como estructura:
//

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class BancoConcurrente {
    private double saldo = 1000;
    private final double saldoInicial = 1000;
    ReentrantLock candado = new ReentrantLock();
    List<String> operaciones = new ArrayList<>();

    private int operacionesRealizadas = 0;
    private int operacionesRechazadas = 0;

    public void retirar(String cliente){
        candado.lock();
        try {

           if (this.saldo - 50 >=  0){
               this.saldo -= 50;
               operacionesRealizadas++;
               operaciones.add(cliente + " - Retirada de 50€ | Saldo resultante: " + this.saldo + "€");
           }else{
               operacionesRechazadas++;
               operaciones.add(cliente + " - RECHAZADA (Saldo insuficiente) | Saldo actual: " + this.saldo + "€");
           }
       }finally {
           candado.unlock();
       }


    }

    public void ingresar(String cliente){
        candado.lock();
        try {
            this.saldo +=20;
            operacionesRealizadas++;
            operaciones.add(cliente + " - Ingreso de 20€ | Saldo resultante: " + this.saldo + "€");
       }finally {
            candado.unlock();
        }


    }

    static void main() {
        BancoConcurrente cuenta = new BancoConcurrente();
        ExecutorService service = Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 10 ; i++) {
            boolean isIngreso = (i % 2 == 0);
            int id = i;
            service.submit(()->{
                String nombreCliente  = "Cliente " + id;
                for (int j = 1; j <= 5 ; j++) {
                    if (isIngreso){
                        cuenta.ingresar(nombreCliente);
                    }else{
                        cuenta.retirar(nombreCliente);
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        service.shutdown();

        try {
            service.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("===== RESUMEN DE OPERACIONES =====");
        for (String registro : cuenta.operaciones) {
            System.out.println(registro);
        }
        System.out.println("\n===== RESUMEN BANCARIO =====");
        System.out.printf("Saldo inicial: %.2f €\n", cuenta.saldoInicial);
        System.out.println("Operaciones realizadas: " + cuenta.operacionesRealizadas);
        System.out.println("Operaciones rechazadas: " + cuenta.operacionesRechazadas);
        System.out.printf("Saldo final: %.2f €\n", cuenta.saldo);
        System.out.println("=============================");
    }
}
