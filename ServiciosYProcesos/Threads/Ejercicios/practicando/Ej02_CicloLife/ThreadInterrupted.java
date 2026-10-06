package Threads.Ejercicios.practicando.Ej02_CicloLife;

public class ThreadInterrupted extends Thread{
    public void run(){

        int cont = 1;
        while (! Thread.currentThread().isInterrupted()){

            System.out.println("Hilo sigue vivo en el while, contador: " +cont);
            cont ++;
            try {
                Thread.sleep(200);

            } catch (InterruptedException e) {
                System.out.println("Capturado, cerrando tranquilo");
                break;

            }
        }
    }

    static void main() {
        ThreadInterrupted hilo = new ThreadInterrupted();
        hilo.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        hilo.interrupt();

    }
}
