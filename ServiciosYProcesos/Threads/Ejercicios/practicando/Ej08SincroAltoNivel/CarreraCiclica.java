package Threads.Ejercicios.practicando.Ej08SincroAltoNivel;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarreraCiclica {

    static void main() {
        int numCorredores = 5;
        // Creamos la barrera para 5 hilos. Cuando los 5 llamen a await(), se abre.
        CyclicBarrier barrera = new CyclicBarrier(numCorredores, () -> {
            System.out.println("\n¡¡TODOS LISTOS EN LA SALIDA... Pum! ¡COMIENZA LA CARRERA!!\n");
        });

        ExecutorService servicio = Executors.newFixedThreadPool(numCorredores);
        Random random = new Random();

        for (int i = 1; i <= numCorredores; i++) {
            int idCorredor = i;
            servicio.submit(() -> {
                try {
                    System.out.println("Corredor " + idCorredor + " ha llegado a la línea de salida.");

                    // Espera a que los demás corredores lleguen aquí
                    barrera.await();

                    // A partir de aquí, todos corren a la vez
                    System.out.println("Corredor " + idCorredor + " está corriendo...");
                    Thread.sleep(random.nextInt(500, 2000)); // Tiempo aleatorio de carrera

                    System.out.println("-> ¡Corredor " + idCorredor + " ha cruzado la meta!");

                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        servicio.shutdown();
    }
}