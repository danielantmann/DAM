package Threads.Ejercicios.PracticaExamen;
//Ejercicio Examen A: El Procesador de Pagos (ExecutorService + Callable + Manejo de Errores)
//Enunciado:
//Crea un programa que gestione el procesamiento de 4 transacciones bancarias en paralelo utilizando un ExecutorService con un pool fijo de 2 hilos (Executors.newFixedThreadPool(2)).
//
//Cada transacción será un Callable<String> que reciba un identificador de transacción (int id) y un importe (double cantidad).
//
//Dentro de la tarea, simula un tiempo de proceso aleatorio o fijo de 400 ms con Thread.sleep().
//
//Requisito extra: Si la cantidad a procesar es mayor a 1000 €, la tarea debe lanzar una excepción simulando "Fondos sospechosos". El Callable debe capturarla o dejar que el Future la gestione.
//
//En el main, lanza las 4 tareas, almacena los Future, recóorrelos imprimiendo el resultado (o el error si falló) mediante .get(), y cierra el executor con shutdown().

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class ProcesoPagos implements Callable<String> {
    private int id;
    private double cantidad;

    public ProcesoPagos(int id , double cantidad) {
        this.id = id;
        this.cantidad = cantidad;
    }

    @Override
    public String call() throws Exception {

        Thread.sleep(400);
        if (this.cantidad > 1000){
            throw new Exception("Fondos sospechosos para la transacción " + this.id);
        }

            return "Transaccion id: " + this.id + "monto: " + this.cantidad ;


    }

    static void main() {
        ExecutorService servicio = Executors.newFixedThreadPool(2);
        List<Future<String>> listaFuturos = new ArrayList<>();
        Random numeroRandom = new Random();

        for (int i = 1; i <= 4 ; i++) {
            ProcesoPagos transaccion = new ProcesoPagos(i, numeroRandom.nextDouble(200,2000));
            listaFuturos.add(servicio.submit(transaccion));
        }

        servicio.shutdown();
        for (Future<String> transacion : listaFuturos){
            try {
                System.out.println(transacion.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                System.out.println("Error en transacción: " + e.getCause().getMessage());
            }
        }
    }
}
