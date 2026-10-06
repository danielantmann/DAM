package Threads.Ejercicios.Asincronia;

import java.util.concurrent.CompletableFuture;

public class Ej08 {
    public static int consultarTemperatura(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return 25;
    }

    public static int consultarHumedad(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
       return 60;
    }
    static void main() {
        CompletableFuture<Integer> tarea1  = CompletableFuture.supplyAsync(()->{
            return consultarTemperatura();
        });
        CompletableFuture<Integer> tarea2  = CompletableFuture.supplyAsync(()->{
            return consultarHumedad();
        });

        CompletableFuture<Void> proceso = tarea1
                .thenCombine(tarea2,(temp, hum)->{
                    return "Temperatura: " + temp + " ºC\nHumedad: " + hum + " %";
                })
                        .thenAccept(texto -> System.out.println(texto));

        proceso.join();
        System.out.println("temp: " + tarea1);
        System.out.println("humedad: " + tarea2);
    }
}
