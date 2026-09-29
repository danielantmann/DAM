package Threads.Ejercicios.EjerciociosClase.Ej3;

//Ejercicio 3.2 — El contador arreglado
//Soluciona el ejercicio 3.1 añadiendo synchronized al método incrementar().
// Verifica que ahora el resultado siempre es 100.000.

public class ContadorFixed {
    private int contador= 0;

    public synchronized void incrementar(){
        contador ++;
    }

    public  synchronized int getContador() {
        return contador;
    }

    public static void main(String[] args) throws InterruptedException {
       ContadorFixed c = new ContadorFixed();

        Runnable task = () ->{
            for (int i = 0; i < 10000; i++) c.incrementar();
        };

        Thread [] hilos = new Thread[10];
        for (int i = 0; i < 10; i++) hilos[i] = new Thread(task);
        for (Thread t : hilos) t.start();
        for (Thread t : hilos) t.join();

        System.out.println("Resultado (esperado 100000): " + c.getContador());
    }

}
