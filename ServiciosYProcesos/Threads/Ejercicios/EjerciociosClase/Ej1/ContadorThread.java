package Threads.Ejercicios.EjerciociosClase.Ej1;
//Ejercicio 1.1 — "Descarga simultánea de archivos"
//Simula la descarga de 4 archivos en paralelo. Cada archivo debe descargarse en su propio hilo, imprimiendo su progreso cada 200 ms hasta llegar al 100%.
//
//Requisitos:
//
//Implementa una versión usando una clase que extienda Thread.
//Implementa otra versión usando una clase que implemente Runnable, lanzada con new Thread(runnable).start().
//Cada hilo debe imprimir su nombre (Thread.currentThread().getName()) y el progreso.
//Compara ambos enfoques en un comentario: ¿cuándo conviene cada uno?
//Pista: usa Thread.ofPlatform().name("descarga-" + i).start(runnable) (API de hilos con nombre de Java 21+, disponible en 25) como alternativa moderna a new Thread(...).

public class ContadorThread  extends Thread{
    @Override
    public void run() {
        for (int i = 1; i <= 10 ; i++) {
            System.out.println(getName() + "  "  + i);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        ContadorThread cont1 = new ContadorThread();
        ContadorThread cont2 = new ContadorThread();
        cont1.start();
        cont2.start();
    }
}
