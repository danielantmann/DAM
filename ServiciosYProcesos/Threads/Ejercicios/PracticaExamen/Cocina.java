package Threads.Ejercicios.PracticaExamen;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Cocina {
    private final ArrayBlockingQueue<String> mostrador = new ArrayBlockingQueue<>(3);

    public void ponerPlato(String plato){
        try {
            this.mostrador.put(plato);
            System.out.println("Plato : " + plato + "puesto en el mostrador");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void retirarPlato(){
        try {
            this.mostrador.take();
            System.out.println("Plato retirado del mostrador");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static void main() {
        Cocina cocina = new Cocina();

        for (int i = 0; i < 2; i++) {

            Thread cocinero = new Thread(()->{
                for (int j = 0; j < 4; j++) {
                    String plato = String.valueOf(j);
                    cocina.ponerPlato(plato);
                }
            });
        cocinero.start();
        }

        for (int i = 0; i < 2; i++) {

            Thread camarero = new Thread(()->{
                for (int j = 0; j < 4; j++) {
                    cocina.retirarPlato();
                }
            });
            camarero.start();
        }
    }

}
