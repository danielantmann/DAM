package Threads.Ejercicios.practicando.Ej01_contadores;

public class ContadorThread extends Thread{

    public  void run(){
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
        ContadorThread contador1 = new ContadorThread();
        ContadorThread contador2 = new ContadorThread();
        contador1.start();
        contador2.start();
    }
}

