package Threads.Ejercicios.EjerciociosClase.Ej2;

//Ejercicio 2.2 — join() en acción
//Lanza tres hilos que simulan descargas de archivos (con Thread.sleep de duración aleatoria).
// El hilo principal debe esperar a que todos terminen (usando join()) antes de imprimir "Todas las descargas han finalizado".

import java.util.Random;

public class DescargasJoin {
    public static void main(String[] args) throws InterruptedException {
        Thread[] descargas = new Thread[3];
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            int id = i +1;

            descargas[i] = new Thread(()->{
            int duracion = 1000 + random.nextInt(2000);
                System.out.println("descarga " + id + "inicida, duracion: " + duracion);
                try {
                    Thread.sleep(duracion);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            descargas[i].start();
        }

        for (Thread t : descargas){
            t.join();
        }
        System.out.println("Todas las descargas finalizaron");
    }
}
