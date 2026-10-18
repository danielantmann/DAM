package Threads.Ejercicios.PracticaExamen;

public class CocineroTimerCancelable extends Thread{
    private int tiempoPlato = 10;

    @Override
    public void run() {
        while(tiempoPlato > 0){
            tiempoPlato --;
            try {
                System.out.println("Estado del cocinero durmiendo: " + this.getState());
                Thread.sleep(1000);
                System.out.println("estado: " + this.getState());
            } catch (InterruptedException e) {
                System.out.println("Estado: " + this.getState()) ;
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    static void main() {
        CocineroTimerCancelable cocinero = new CocineroTimerCancelable();

        System.out.println("estado: " + cocinero.getState());
        cocinero.start();


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        cocinero.interrupt();

        System.out.println("Estado: " +cocinero.getState());
        try {
            cocinero.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Estado: " +cocinero.getState());

    }
}
