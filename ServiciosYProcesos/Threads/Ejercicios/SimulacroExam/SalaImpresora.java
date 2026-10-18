package Threads.Ejercicios.SimulacroExam;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

//Sala de impresoras — 25 minutos (2 puntos)
//Una empresa tiene dos impresoras y ocho empleados que desean imprimir documentos.
//Cada empleado debe:
//        1. Solicitar acceso a una impresora.
//2. Esperar entre uno y dos segundos mientras imprime.
//        3. Liberar el recurso.
//4. Mostrar su identificador y el nombre del hilo.
//Utiliza ExecutorService y Semaphore(2).
//Añade un contador de impresiones en curso y muestra cuántas hay activas. El valor nunca debe superar dos.
//Importante: el permiso se debe liberar de forma segura incluso si una impresión se interrumpe.
public class SalaImpresora {
    static void main() {
        Semaphore impresoras = new Semaphore(2,true);
        AtomicInteger plazasActivas = new AtomicInteger(0);
        final int cantidadEmpleados = 8;
        ExecutorService service = Executors.newFixedThreadPool(cantidadEmpleados);
        Random numeroAleatorio = new Random();
        for (int i = 1; i <= cantidadEmpleados ; i++) {
            int id = i;

            service.submit(()->{
                String nombreHilo = Thread.currentThread().getName();
                try {
                    impresoras.acquire();

                    int plazas = plazasActivas.incrementAndGet();
                    Thread.sleep(numeroAleatorio.nextInt(1000,2000));
                    System.out.println(nombreHilo + "  Plazas activas: " +plazas);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }finally {
                    impresoras.release();
                    plazasActivas.decrementAndGet();
                }
            });

        }
        service.shutdown();
    }
}
