package Threads.Ejercicios.EjerciociosClase.Ej5;

import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

//Ejercicio 5.4 — tryLock con tiempo de espera
//Modifica el ejercicio del deadlock (3.4) para que, en lugar de synchronized, use ReentrantLock con tryLock(tiempo, unidad). Si un hilo no consigue el segundo lock en el tiempo indicado, debe liberar el primero y reintentar más tarde.
public class TryLock {
    private static final ReentrantLock candado1 = new ReentrantLock();
    private static final ReentrantLock candado2 = new ReentrantLock();

    static void tarea(ReentrantLock uno, ReentrantLock dos, String nombre){
        while (true){
            try {
                if (uno.tryLock(200, TimeUnit.MILLISECONDS)){
                    try {

                        if (dos.tryLock(200,TimeUnit.MILLISECONDS)){

                            try {
                                System.out.println(nombre + " consigio ambos locks");
                                return;
                            }finally {
                                dos.unlock();
                            }
                        }else{
                            System.out.println(nombre + " no consigio ambos locks");
                        }
                    }finally {
                        uno.unlock();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);

            }
        }
    }

    public static void main(String[] args) {
        new Thread(()-> tarea(candado1,candado2,"A")).start();
        new Thread(()-> tarea(candado2,candado1,"B")).start();
    }
}
