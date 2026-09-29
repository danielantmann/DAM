package Threads.Ejercicios.EjerciociosClase.Ej1;

public class ContadorRunneable implements Runnable {
    @Override
    public void run() {
        for (int i = 1; i <= 10 ; i++) {
            System.out.println(Thread.currentThread().getName() + "   " + i);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new ContadorRunneable());
        Thread t2 = new Thread(new ContadorRunneable());

        t1.start();
        t2.start();
    }
}
