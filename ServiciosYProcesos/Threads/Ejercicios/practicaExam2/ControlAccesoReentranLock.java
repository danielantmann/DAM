package Threads.Ejercicios.practicaExam2;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

//La Cabina de Control de Acceso
//Imagina que estás programando el sistema de seguridad de un edificio corporativo:
//
//Hay 4 empleados (hilos) que quieren acceder al edificio.
//Todos deben llegar primero a una zona de recepción. Queremos usar una CyclicBarrier para que los 4 empleados esperen en la entrada a que estén todos reunidos antes de pasar al control de seguridad. En cuanto el último llega, se imprime: "¡Todos los empleados están en recepción, pasamos al control!".
//
//Una vez dentro, solo 1 empleado a la vez puede pasar por el torno de seguridad que valida su tarjeta. Para simular esto de forma explícita, debes usar un ReentrantLock (nada de synchronized).
//
//Cada empleado simula el fichaje con un Thread.sleep(300) dentro de la zona protegida por el lock.
//
//Usa un CountDownLatch para que el hilo principal (main) se espere pacientemente a que los 4 empleados hayan terminado de fichar y salir, y al final imprima: "Sistema cerrado. Jornada laboral finalizada."
public class ControlAccesoReentranLock {
    static void main() {
        int numeroEmpleados = 4;
        CyclicBarrier barrera = new CyclicBarrier(numeroEmpleados,()->{
            System.out.println("Todos los empleados listos para entrar");
        });
        CountDownLatch tarea = new CountDownLatch(4);
        ReentrantLock candado = new ReentrantLock();
        ExecutorService empleados = Executors.newFixedThreadPool(4);

        for (int i = 1; i <= 4 ; i++) {
            int id = i;
            empleados.submit(()->{
                try {
                    barrera.await();
                    candado.lock();

                    Thread.sleep(500);
                    System.out.println("empleado " + id + "pasa por el torno" );

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }finally {
                    candado.unlock();
                }
                tarea.countDown();
            });

        }
        try {
            tarea.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Jornado finalizada");
    empleados.shutdown();
    }


}
