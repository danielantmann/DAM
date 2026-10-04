package Threads.Ejercicios.EjerciociosClase.Ej7;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CalculadoraCubos {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService trabajadores = Executors.newFixedThreadPool(2);
        Integer[] numeros = {3,5,7,9};
        List<Future<Integer>> resultados = new ArrayList<>();

        for (Integer numero : numeros){
            //Future<Integer> resultado = trabajadores.submit(()-> numero * numero*numero);
            resultados.add(trabajadores.submit(()-> numero * numero*numero));
        }
        for (Future<Integer> resultado: resultados){
            System.out.println("Resultado: " + resultado.get());
        }

        trabajadores.shutdown();
    }
}
