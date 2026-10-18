package Threads.Ejercicios.PracticaExamen;

public class CuentaRegrsiva extends Thread{
    private  int numero = 10;

    public void run(){
        for (int i = 0; i < 10; i++) {
            numero --;
            System.out.println("cuenta regresiva: " + numero);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    static void main() {
        CuentaRegrsiva c = new CuentaRegrsiva();
        c.start();

        try {
            c.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Despegue");
    }

}
