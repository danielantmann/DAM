package Threads.Ejercicios.EjerciociosClase.Ej6;

import java.util.concurrent.ArrayBlockingQueue;

//El Reto: El Centro de Distribución (Multi-productor y Multi-consumidor)
//
//Crea una ArrayBlockingQueue<String> con una capacidad máxima de 4 huecos.
//
//        Crea 2 hilos Productores (puedes llamarlos "Productor-1" y "Productor-2"). Cada uno debe fabricar y meter 4 paquetes en la cola (por ejemplo: "Paquete A1", "Paquete A2", etc.). Usa put().
//
//Crea 2 hilos Consumidores (por ejemplo, "Repartidor-1" y "Repartidor-2"). Cada uno debe encargarse de sacar y procesar 4 paquetes de la cola usando take(). (Ojo: entre los dos consumidores tienen que sacar los 8 paquetes totales).
//
//Arranca todos los hilos con .start() y hazles un .join() al final para que el programa espere a que termine todo el reparto.
public class CentroDistribucion {
    public static void main(String[] args) throws InterruptedException {
        ArrayBlockingQueue<String> baul = new ArrayBlockingQueue<>(4);

        Thread productor1 = new Thread(()->{
            for (int i = 1; i <= 4 ; i++) {
                System.out.println("Fabricando: A" + Integer.toString(i));
                try {
                    baul.put("A"+ Integer.toString(i));
                    System.out.println("Guardado en el baul: A" + i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread productor2 = new Thread(()->{
            for (int i = 1; i <= 4 ; i++) {
                System.out.println("Fabricando: A" + Integer.toString(i));
                try {
                    baul.put("A"+ Integer.toString(i));
                    System.out.println("Guardado en el baul: A" + i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread repartidor1 = new Thread(()->{
            for (int i = 1; i <=4 ; i++) {
                try {
                    String valor = baul.take();
                    System.out.println("sacando " + valor);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        Thread repartidor2 = new Thread(()->{
            for (int i = 1; i <=4 ; i++) {
                try {
                    String valor = baul.take();
                    System.out.println("sacando " + valor);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

       productor1.start();
       productor2.start();
        repartidor1.start();
        repartidor2.start();

        productor1.join();
       productor2.join();

       repartidor1.join();
       repartidor2.join();
    }
}
