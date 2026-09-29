package Threads.Ejercicios.EjerciociosClase.Ej5.ParkingSeparacionClases;

import java.util.concurrent.Semaphore;

public class Parking {
    private final Semaphore plazas;


    public Parking(int totalPlazas) {
        this.plazas = new Semaphore(totalPlazas, true);

    }

    public void entrar(int cocheId) throws InterruptedException {
        System.out.println("Coche " + cocheId + " esparando plaza" );
        plazas.acquire();
        System.out.println("Coche " + cocheId + " aparcado. Plazas libres: " + plazas.availablePermits());

    }

    public void salir(int cocheId){
        plazas.release();
        System.out.println("Coche " + cocheId + " se va. Plazas libres: " + plazas.availablePermits());
    }
    }

