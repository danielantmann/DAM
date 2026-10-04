package Threads.Ejercicios.EjerciociosClase.Ej6;
//¿De qué va el ejercicio?
//Imagina una empresa donde hay un buzón de tareas pendientes.
//
//Un solo Productor (El Jefe) crea 10 tareas (por ejemplo: "Tarea 1", "Tarea 2", etc.) y las mete en una ArrayBlockingQueue de tamaño 3.
//
//Tres Trabajadores (Consumidores) están mirando constantemente el buzón. En cuanto hay una tarea libre, el primero que esté libre la coge, la "procesa" (simulamos que tarda un par de segundos con un Thread.sleep) y vuelve a mirar el buzón.

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class TareasPendientes {
    public static void main(String[] args) throws InterruptedException {
        ArrayBlockingQueue<Integer> buzonTareas = new ArrayBlockingQueue<>(3);
        Thread jefe = new Thread(()->{
            for (int i = 1; i <= 10 ; i++) {
                System.out.println("Jefe crea tarea" +i);
                try {
                    buzonTareas.put(i);
                    System.out.println("Jefe guarda la tarea" +i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread[] trabajadores = new Thread[3];

        for (int i = 0; i < 3 ; i++) {
            trabajadores[i] = new Thread(()->{
                for (int j = 0; j <4 ; j++) {
                    try {

                        int valor = buzonTareas.take();
                        System.out.println("trabajador pillando tarea: " + valor  );
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            });
        }

        jefe.start();
        for (Thread t: trabajadores) t.start();

        jefe.join();
        for (Thread t: trabajadores) t.join();

        }

    }





