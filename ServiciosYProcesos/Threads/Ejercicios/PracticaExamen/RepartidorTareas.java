package Threads.Ejercicios.PracticaExamen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class RepartidorTareas implements Callable<String> {
    private String nombre;
    private String precio;
    private Random num = new Random();
    public RepartidorTareas(String nombre,String precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    @Override
    public String call() throws Exception {

        Thread.sleep(num.nextInt(1000,3000));
        return "Producto " + this.nombre + "procesado, percio : " + precio;
    }

    static void main() {
        ExecutorService servicio = Executors.newFixedThreadPool(3);
        List<Future<String>> listaProductos = new ArrayList<>();

        for (int i = 1; i <= 5 ; i++) {
            RepartidorTareas tarea = new RepartidorTareas(String.valueOf(i),String.valueOf(i+10));
            Future<String> producto= servicio.submit(tarea);
            listaProductos.add(producto);
        }
        servicio.shutdown();

        for(Future<String> producto : listaProductos){
            try {
                System.out.println(producto.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
