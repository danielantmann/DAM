package Threads.Ejercicios.practicando.Ej07PoolHilos;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CallableFutureFactorial implements Callable<Integer> {
    private final int numero;

    public CallableFutureFactorial(int numero) {
        this.numero = numero;
    }

    @Override
    public Integer call() throws Exception {
        int resultado = 1;
        for (int i = 1; i <= numero; i++) {
            resultado *= i;
        }
        return resultado;
    }

    static void main() {
        ExecutorService servicio = Executors.newFixedThreadPool(5);
        List<Future<Integer>> futuros = new ArrayList<>();

        // 1. Enviamos todas las tareas y guardamos los Future
        for (int i = 1; i <= 5; i++) {
            CallableFutureFactorial tarea = new CallableFutureFactorial(i);
            futuros.add(servicio.submit(tarea));
        }

        // 2. Recogemos los resultados después
        for (int i = 0; i < futuros.size(); i++) {
            try {
                System.out.println("Factorial de " + (i + 1) + " es: " + futuros.get(i).get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        servicio.shutdown();
    }
}