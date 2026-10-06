package Threads.Ejercicios.practicando.Ej06BlockinQueue;

import java.util.concurrent.ConcurrentHashMap;

public class CacheCoimpartida {
    private ConcurrentHashMap<String, Integer> compartido = new ConcurrentHashMap<>();



    static void main() {
        CacheCoimpartida cacheCoimpartida = new CacheCoimpartida();

        for (int i = 0; i < 5; i++) {

            int num = i;
            Thread hilo = new Thread(()->{
              cacheCoimpartida.compartido.put("Clave: " + num , num +10);

              Integer valor = cacheCoimpartida.compartido.get("Clave: " +num);
                System.out.println("Hilo " + num + "leyo: " + valor);
            });
            hilo.start();
        }
    }
}
