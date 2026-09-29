package Threads.Ejercicios.EjerciociosClase.Ej5.Piscina;

import java.util.concurrent.Semaphore;

public class Piscina {
    private final Semaphore plazas;


    public Piscina(int totalPlazas) {
        this.plazas = new Semaphore(totalPlazas, true);
    }

    public void entrar(int nadadorId) throws InterruptedException {
        System.out.println("Nadador: " + nadadorId + " llego a la piscina");
        plazas.acquire();
        System.out.println("Nadador: " + nadadorId + " entro a la piscina. Plazas: " + plazas.availablePermits());
    }

    public void salir(int nadadorId){
        plazas.release();
        System.out.println("Nadador: " + nadadorId + " salio de la psicina. Plazas: " +plazas.availablePermits());
    }
}
