package Threads.Ejercicios.EjerciociosClase.Ej3;

//
//Ejercicio 3.4 — Deadlock provocado
//Crea dos objetos recursoA y recursoB. Lanza un hilo que bloquee recursoA y luego intente bloquear recursoB, y otro hilo que haga lo contrario (bloquear recursoB y luego recursoA).
// Ejecuta el programa varias veces hasta que se produzca un deadlock.
// Después, corrige el código estableciendo un orden global de adquisición de bloqueos.
public class DeadLock {
    private static final Object recursoA = new Object();
    private static final Object recursoB = new Object();

    public static void main(String[] args) {
        Runnable tarea1 = () -> {
            synchronized (recursoA) {
                System.out.println(Thread.currentThread().getName() + " bloquea A");
                try { Thread.sleep(100); }
                catch (InterruptedException ignored) {}
                synchronized (recursoB) {
                    System.out.println(Thread.currentThread().getName() + " bloquea B");
                }
            }
        };

        // Ambos hilos adquieren SIEMPRE en el mismo orden: A antes que B
        Thread t1 = new Thread(tarea1, "Hilo-1");
        Thread t2 = new Thread(tarea1, "Hilo-2");
        t1.start();
        t2.start();
    }
}
