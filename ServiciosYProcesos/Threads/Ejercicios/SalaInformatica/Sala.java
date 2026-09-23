package Threads.Ejercicios.SalaInformatica;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Sala {
    private final int ORDENADORES = 3;
    private final int NUM_ESTUDIANTES = 10;

    Semaphore ordenadores = new Semaphore(ORDENADORES, true);

    ArrayList<Estudiante> listaEstudiantes = new ArrayList<>();

    public void iniciarSimulacion(){
        for (int i = 1; i <= NUM_ESTUDIANTES; i++) {
            Estudiante estudiante = new Estudiante("Estudiante " + i , ordenadores);
            listaEstudiantes.add(estudiante);
            estudiante.start();
        }
        for (Estudiante estudiante:listaEstudiantes){
            try {
                estudiante.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
