package Threads.Ejercicios.SimulacroExam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ProcesarNotas {
    static void main() {
        List<Integer> notas = List.of(6, 8, 4, 9, 7);
        List<Future<Integer>> notasProcesadas = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(3);

        for (int i = 0; i < notas.size(); i++) {
            int index = i;
            Future<Integer> nota = service.submit(()->{
                try {
                    Thread.sleep(500);
                    return notas.get(index);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            notasProcesadas.add(nota);

        }

        service.shutdown();

        try {
            service.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int resultado = 0;
        for (Future<Integer> nota : notasProcesadas){
            try {
                resultado += nota.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        double promedio = (double)resultado / notas.size();

        System.out.println("Notas: " + notas.toString().replace("[", "").replace("]", ""));
        System.out.println("Promedio: " + promedio);
    }
}
