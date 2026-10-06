package Threads.Ejercicios.practicando.Ej03CarraraSync;

public class ContadorRoto extends Thread{
    private  int contador = 0;

    public int getContador() {
        return contador;
    }

    public void incremetar(){
        this.contador ++;
    }

    public void run(){
        for (int i = 0; i <10000 ; i++) {
            incremetar();
        }
    }


    static void main() {
        Thread[] listaHilos = new Thread[10];
        ContadorRoto contador = new ContadorRoto();
        for (int i = 1; i <= 10 ; i++) {

            listaHilos[i -1] = new Thread(contador);

        }
        for (Thread hilo : listaHilos){

                hilo.start();

        }
        for (Thread hilo : listaHilos){
            try {
                hilo.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Resultado final del contador: " + contador.getContador());
    }

}
