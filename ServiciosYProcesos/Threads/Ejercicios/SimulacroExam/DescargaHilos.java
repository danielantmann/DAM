package Threads.Ejercicios.SimulacroExam;

//
//Descargas con hilos — 15 minutos (1 punto)
//Crea tres hilos que simulen la descarga de tres archivos.
//        - Archivo A: tarda 2 segundos.
//- Archivo B: tarda 3 segundos.
//- Archivo C: tarda 1 segundo.
//Cada hilo debe imprimir cuándo empieza y cuándo termina, incluyendo su nombre.
//El hilo principal debe esperar a que terminen las tres descargas antes de mostrar:
//Todas las descargas han finalizado
//
//
//        Utiliza Runnable, Thread, sleep() y join().

import java.time.LocalTime;

public class DescargaHilos {

    static void main() {
            Thread[] hilos = new Thread[3];



            hilos[0] = new Thread(()->{
            String nombre = Thread.currentThread().getName();
                System.out.println(nombre + " empieza a las " + LocalTime.now());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(nombre + " termina a las " + LocalTime.now());

            }, "Archivo a");

        hilos[1] = new Thread(()->{
            String nombre = Thread.currentThread().getName();
            System.out.println(nombre + " empieza a las " + LocalTime.now());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(nombre + " termina a las " + LocalTime.now());

        }, "Archivo B");

        hilos[2] = new Thread(()->{
            String nombre = Thread.currentThread().getName();
            System.out.println(nombre + " empieza a las " + LocalTime.now());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(nombre + " termina a las " + LocalTime.now());

        }, "Archivo c");

        for (Thread hilo : hilos){
                hilo.start();
        }

        for (Thread hilo : hilos){
            try {
                hilo.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Todas las descargas acabaron");

    }
}
