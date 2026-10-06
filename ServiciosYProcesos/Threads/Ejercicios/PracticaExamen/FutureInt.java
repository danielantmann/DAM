package Threads.Ejercicios.PracticaExamen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class FutureInt implements Callable<Integer> {
    private int numero;

    public FutureInt(int numero) {
        this.numero = numero;
    }

    @Override
    public Integer call() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return numero * numero;
    }


    static void main() {
        ExecutorService executor = Executors.newFixedThreadPool(3);


        List<Future<Integer>> listaFuture = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            FutureInt futuro = new FutureInt(i);
            Future<Integer> resultado  = executor.submit(futuro);
            listaFuture.add(resultado);

        }
        executor.shutdown();
        for (Future<?> numero : listaFuture){
            try {
                System.out.println("resultado : " + numero.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
