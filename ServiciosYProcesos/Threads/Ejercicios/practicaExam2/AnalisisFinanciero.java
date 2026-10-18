package Threads.Ejercicios.practicaExam2;

//El Centro de Análisis Financiero (Callable y Future)
//Imagina que estás programando un sistema donde el banco necesita calcular los beneficios de 3 sucursales diferentes en paralelo:
//
//El trabajo (Callable<Double>):
//
//Vas a crear 3 tareas (una para cada sucursal).
//
//Cada sucursal procesa sus datos simulando un retraso con Thread.sleep(400) y al final devuelve un número Double con los beneficios obtenidos (por ejemplo: Sucursal 1 gana 1500.50, Sucursal 2 gana 2300.00, etc.).
//
//El lanzamiento (ExecutorService):
//
//Usas un ExecutorService (de tamaño 3) para enviar los tres Callable.
//
//Al hacer el .submit(), el executor te devolverá objetos de tipo Future<Double> para cada tarea.
//
//La recogida de resultados (El hilo main):
//
//El hilo principal tiene que recorrer esos Future (o esperarlos), recuperar el valor que ha devuelto cada hilo usando el método .get(), ir sumándolos todos, y al final imprimir por pantalla el beneficio total acumulado de las tres sucursales.

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AnalisisFinanciero {
    static void main() {
        List<Future<Double>> analisisFinal = new ArrayList<>();
        ExecutorService servicio = Executors.newFixedThreadPool(3);
        Random numAleatorio = new Random();
        for (int i = 0; i <= 3; i++) {
            Future<Double> resultado = servicio.submit(()->{
                try {
                    Thread.sleep(500);
                    Double cantidad = numAleatorio.nextDouble(50,1000);
                    return cantidad;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });
            analisisFinal.add(resultado);
        }
        servicio.shutdown();

        int indice = 1;
        for (Future<Double> r: analisisFinal){
            try {
                System.out.println("socursal "+indice+ " gana: " +r.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            indice ++;
        }
    }

}
