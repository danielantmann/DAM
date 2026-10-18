package Threads.Ejercicios.SimulacroExam;

//Procesamiento de facturas — 35 minutos (3,5 puntos)
//Una empresa necesita procesar cinco facturas mediante un pool de tres hilos.
//Cada factura tiene un importe:
//double[] importes = {120.0, 75.5, 240.0, 18.5, 90.0};
//
//
//Para cada factura debes:
//        - Enviar una tarea con submit().
//        - Utilizar Callable<Double> para devolver el importe con un IVA del 21 %.
//        - Simular un tiempo de procesamiento de entre uno y dos segundos.
//- Guardar cada resultado en un Future<Double>.
//        - Recuperar los resultados y mostrar el importe original y el importe con IVA.
//- Calcular el total final.

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PocesoFacturas {
    static void main() {
        double[] importes = {120.0, 75.5, 240.0, 18.5, 90.0};
        List<Future<Double>> listaResultados = new ArrayList<>();
        ExecutorService servicio  = Executors.newFixedThreadPool(3);

        for (int i = 0; i < importes.length; i++) {
            double importeSinIva = importes[i];
            Future<Double> resultado = servicio.submit(()->{
                Thread.sleep(500);
                return importeSinIva + (importeSinIva * 0.21);
            });
            listaResultados.add(resultado);
        }

        servicio.shutdown();

        try {
            servicio.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        int idFactura = 1;
        double total = 0;

        for (int i = 0; i < listaResultados.size(); i++) {
            try {
                // .get() nos devuelve el valor calculado por el Callable
                double importeConIva = listaResultados.get(i).get();
                double original = importes[i];

                System.out.println("Factura " + idFactura + " - Original: " + original + " € | Con IVA: " + importeConIva + " €");

                total += importeConIva;
                idFactura++;

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Total: " + total);
    }
}
