package Threads.Ejercicios.EjerciociosClase.Ej7;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CalculadoraRecurrente {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService trabajadores = Executors.newFixedThreadPool(3);
        int numeros [] = {2,4,5};
        List<Future<Integer>> resultados = new ArrayList<>();

        for(Integer num: numeros){
            Future<Integer> resultado = trabajadores.submit(()-> num * num);
            resultados.add(resultado);
        }

        for (Future<Integer> resultado: resultados){
            System.out.println("Resultado: " + resultado.get());
        }

        trabajadores.shutdown();;
    }
}
