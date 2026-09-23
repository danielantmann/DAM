package Threads.Ejercicios.SalaInformatica;

import java.util.concurrent.Semaphore;

public class Estudiante extends Thread{
    private final String nombre;
    private final Semaphore ordenador;

    public Estudiante(String nombre, Semaphore ordenador){
        this.nombre = nombre;
        this.ordenador = ordenador;
    }

    public void run(){

        try {
            System.out.println("Estudiante " + nombre + " llego al aula");
            ordenador.acquire();
            System.out.println("Estudiante " + nombre + " se sento en el ordenador");
            Thread.sleep(2000);

            System.out.println("Estudiante " + nombre + " salio  del aula");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            ordenador.release();
        }
    }
}
