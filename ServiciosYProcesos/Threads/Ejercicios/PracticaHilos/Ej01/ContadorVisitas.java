package Threads.Ejercicios.PracticaHilos.Ej01;

import java.util.concurrent.atomic.AtomicInteger;

public class ContadorVisitas {
    private  int contador = 0;
    private final AtomicInteger contadorAtomico = new AtomicInteger(0);;

    public AtomicInteger getContadorAtomico() {
        return contadorAtomico;
    }

    public int getContador() {
        return contador;
    }

    public  void incrementarContador(){
        this.contador ++;
    }

    public synchronized void incrementarContadorSync(){
        this.contador ++;
    }

    public void incrementarContadorAtomico(){
        contadorAtomico.incrementAndGet();
    }
}
