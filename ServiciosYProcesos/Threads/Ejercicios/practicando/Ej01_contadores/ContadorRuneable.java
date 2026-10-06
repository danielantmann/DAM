package Threads.Ejercicios.practicando.Ej01_contadores;

public class ContadorRuneable implements Runnable{
    @Override
    public void run() {
        for (int i = 1; i <= 10 ; i++) {
            System.out.println(i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static void main() {
        ContadorRuneable tarea = new ContadorRuneable();

        Thread contador1 = new Thread(tarea);
        Thread contador2 = new Thread(tarea);

        contador1.start();
        contador2.start();
    }
}
