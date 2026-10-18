package Threads.Ejercicios.SimulacroExam;

//Resumen asíncrono del restaurante — 35 minutos (3,5 puntos)
//Una vez finalizados los pedidos, simula tres consultas independientes:
//Consultar pedidos completados → devuelve 15
//Consultar ingresos             → devuelve 325.50
//Consultar cocineros activos    → devuelve 0
//
//
//Requisitos:
//        - Cada método debe devolver un CompletableFuture.
//        - Utiliza supplyAsync() y un executor propio.
//        - Las tres consultas deben poder ejecutarse en paralelo.
//        - Utiliza CompletableFuture.allOf() para esperar a que todas terminen.
//        - Recupera y presenta los resultados de forma conjunta.
//        - Gestiona las excepciones y cierra el executor correctamente.


import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResumenAsyncro {
    static void main() {
        final int pedidos = 15;
        final double ingreso = 325.50;
        final int cocinerosActivos = 0;
        Random aleatorio = new Random();
        ExecutorService servicio = Executors.newVirtualThreadPerTaskExecutor();
        CompletableFuture<Integer> comprobarPedidos  = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(aleatorio.nextInt(100,500));
                return pedidos;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },servicio);

        CompletableFuture<Double> comprobarIngresos  = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(aleatorio.nextInt(100,500));
                return ingreso;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },servicio);

        CompletableFuture<Integer> comprobarCocineros  = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(aleatorio.nextInt(100,500));
                return cocinerosActivos;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },servicio);

        CompletableFuture<Void> todasTareas = CompletableFuture.allOf(comprobarPedidos,comprobarIngresos,comprobarCocineros);

        CompletableFuture<String> resultadoFinal = todasTareas.thenApply(v->{
          try {
              int pedidosOk = comprobarPedidos.join();
              double ingresosOk = comprobarIngresos.join();
              int cocinerosOk = comprobarCocineros.join();
              return  "===== RESUMEN DEL RESTAURANTE =====\n" +
                      "Pedidos completados: " + pedidosOk + "\n" +
                      "Ingresos: " + ingresosOk + " €\n" +
                      "Cocineros activos: " + cocinerosOk + "\n" +
                      "==================================";
          } catch (Exception e) {
              throw new RuntimeException(e);

          }
        });

        resultadoFinal.thenAccept(System.out::println).join();
        servicio.shutdown();
    }

}
