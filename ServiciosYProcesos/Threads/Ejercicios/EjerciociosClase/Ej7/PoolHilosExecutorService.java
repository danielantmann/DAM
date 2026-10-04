package Threads.Ejercicios.EjerciociosClase.Ej7;
//Ejercicio 7.1 — Pool fijo de hilos
//Crea un ExecutorService con un pool de 4 hilos y envíale 20 tareas Runnable que simulan procesar un pedido
// (imprimir mensaje + dormir un tiempo aleatorio). Cierra el pool correctamente al finalizar.



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PoolHilosExecutorService {


    public static void main(String[] args) {
        ExecutorService trabajadores = Executors.newFixedThreadPool(4);

        for (int i = 1; i <= 20 ; i++) {
            int pedido = i;
            trabajadores.submit(()->{
                System.out.println("Procesando pedido " + pedido
                        + " en el hilo " + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        trabajadores.shutdown();
    }

}
