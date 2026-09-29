package Threads.Ejercicios.EjerciociosClase.Ej5.Piscina;

import java.util.Random;

public class MainPiscina {
    public static void main(String[] args) {
        Piscina piscina = new Piscina(4);
        Random random = new Random();

        for (int i = 1; i <= 10 ; i++) {
            Thread nadador = new Thread(new Nadador(i,piscina));
            nadador.start();
            try {
                Thread.sleep(random.nextInt(300));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
