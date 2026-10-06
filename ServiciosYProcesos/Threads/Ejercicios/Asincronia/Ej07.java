package Threads.Ejercicios.Asincronia;

import java.util.concurrent.CompletableFuture;

public class Ej07 {

    public  static CompletableFuture<String> obtenerUsuario(){
        return CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
           return "Oscar";
        });
    }

    public static CompletableFuture<String> devolverMail (String usuario){
        return CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return  usuario + "@example";
        });
    }

    static void main() {
        System.out.println("Iniciando");
        CompletableFuture<String>  resultado = obtenerUsuario()
                .thenCompose(usuario -> devolverMail(usuario));

        System.out.println("Resultado: " + resultado.join());
    }
}
