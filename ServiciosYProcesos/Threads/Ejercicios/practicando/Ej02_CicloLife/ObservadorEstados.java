package Threads.Ejercicios.practicando.Ej02_CicloLife;

public class ObservadorEstados extends Thread{

    public void run(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static void main() {
        ObservadorEstados estado  = new ObservadorEstados();
        System.out.println("Estado hilo: " + estado.getState());
        estado.start();
        while (estado.isAlive()){
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Estado hilo: " + estado.getState());
        }
    }
}
