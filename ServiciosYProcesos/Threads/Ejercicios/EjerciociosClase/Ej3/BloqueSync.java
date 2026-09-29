package Threads.Ejercicios.EjerciociosClase.Ej3;

//Ejercicio 3.3 — Bloques sincronizados
//Reescribe el ejercicio 3.2 usando un bloque synchronized(this) en lugar de sincronizar
// el método completo. ¿Cambia el resultado? ¿Qué ventaja tiene sincronizar solo una parte del código?

public class BloqueSync {
    private int contador = 0;

    public void incrementar(){
        synchronized (this){
            contador ++;
        }
    }

    public int getContador() {
        synchronized (this){
            return contador;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BloqueSync bloque = new BloqueSync();

        Runnable task = ()->{
            for (int i = 0; i < 1000; i++) {
                bloque.incrementar();
            }
        };

        Thread[] hilos = new Thread[10];
        for (int i = 0; i < 10; i++) hilos[i] = new Thread(task);
        for (Thread t : hilos) t.start();
        for (Thread t : hilos) t.join();

        System.out.println("Resultado (esperado 100000): " +   bloque.getContador());
    }
}
