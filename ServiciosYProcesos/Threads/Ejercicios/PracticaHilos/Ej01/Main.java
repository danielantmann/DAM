package Threads.Ejercicios.PracticaHilos.Ej01;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {


    public static void main(String[] args) {
        ContadorVisitas contadorNormal = new ContadorVisitas();
        ContadorVisitas contadorSync = new ContadorVisitas();
        ContadorVisitas contadorAtomico = new ContadorVisitas();
        Random numRandom = new Random();
        List<Thread> listaHilos = new ArrayList<>();

        long inicioNormal = System.currentTimeMillis();
        for (int i = 1; i <= 1000 ; i++) {
            Thread hilo = new Thread(()->{
                try {
                    Thread.sleep(numRandom.nextInt(101) + 50);
                    contadorNormal.incrementarContador();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            hilo.start();
            listaHilos.add(hilo);
        }

        for (Thread hilo : listaHilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        listaHilos.clear();
        long tiempoNormal = System.currentTimeMillis() - inicioNormal;

        long inicioSync = System.currentTimeMillis();
        for (int i = 1; i <= 1000 ; i++) {
            Thread hilo = new Thread(()->{
                try {
                    Thread.sleep(numRandom.nextInt(101) + 50);
                    contadorSync.incrementarContadorSync();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            hilo.start();
            listaHilos.add(hilo);
        }

        for (Thread hilo : listaHilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        long tiempoSync = System.currentTimeMillis() - inicioSync;

        listaHilos.clear();

        long inicioAtomico = System.currentTimeMillis();
        for (int i = 1; i <= 1000 ; i++) {
            Thread hilo = new Thread(()->{
                try {
                    Thread.sleep(numRandom.nextInt(101) + 50);
                    contadorAtomico.incrementarContadorAtomico();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            hilo.start();
            listaHilos.add(hilo);
        }

        for (Thread hilo : listaHilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        long tiempoAtomico = System.currentTimeMillis() - inicioAtomico;

        listaHilos.clear();


        System.out.println("=== CONTADOR DE VISITAS WEB ===");
        System.out.println("Esperando 1000 visitantes..");
        System.out.println("");
        System.out.println("--- SIN SINCRONIZACION ---");
        System.out.println("visitas esperadas: 1000");
        System.out.println("visitas contdas: " + contadorNormal.getContador()+
                (contadorNormal.getContador() == 1000 ? "  ✅ CORRECTO" : "  ❌ INCORRECTO"));
        System.out.println("Tiempo: " + tiempoNormal + "ms");
        System.out.println("");
        System.out.println("--- CON SINCRONIZACION ---");
        System.out.println("visitas esperadas: 1000");
        System.out.println("visitas contdas: " + contadorSync.getContador()+
                (contadorSync.getContador() == 1000 ? "  ✅ CORRECTO" : "  ❌ INCORRECTO"));
        System.out.println("Tiempo: " + tiempoSync + "ms");
        System.out.println("");
        System.out.println("--- CON ATOMICINTEGER ---");
        System.out.println("visitas esperadas: 1000");
        System.out.println("visitas contdas: " + contadorAtomico.getContadorAtomico().get()+
                (contadorAtomico.getContadorAtomico().get() == 1000 ? "  ✅ CORRECTO" : "  ❌ INCORRECTO"));
        System.out.println("Tiempo: " + tiempoAtomico + "ms");
    }
}
