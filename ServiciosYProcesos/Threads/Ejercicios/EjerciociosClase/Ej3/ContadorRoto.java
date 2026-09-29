package Threads.Ejercicios.EjerciociosClase.Ej3;

//Ejercicio 3.1 — El contador roto
//Crea una clase Contador con un método incrementar() que haga contador++.
// Lanza 10 hilos que llamen 10.000 veces cada uno a incrementar().

public class ContadorRoto {
    private int contador = 0;

    public void incrementar(){
        contador ++;
    }

    public int getContador() {
        return contador;
    }

    public static void main(String[] args) throws InterruptedException {
        ContadorRoto c = new ContadorRoto();
        Runnable tarea = () -> {
            for (int i = 0; i < 10_000; i++) c.incrementar();
        };

        Thread[] hilos = new Thread[10];
        for (int i = 0; i < 10; i++) hilos[i] = new Thread(tarea);
        for (Thread t : hilos) t.start();
        for (Thread t : hilos) t.join();

        System.out.println("Resultado (esperado 100000): " + c.getContador());
    }
    }


