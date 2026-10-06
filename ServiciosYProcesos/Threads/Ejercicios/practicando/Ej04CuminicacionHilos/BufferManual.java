package Threads.Ejercicios.practicando.Ej04CuminicacionHilos;

public class BufferManual {
    private int contenido;
    private boolean lleno = false;
    public synchronized void producir(int valor){

        while(lleno){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        contenido = valor;
        lleno = true;
        System.out.println("Producido: " + valor);
        notifyAll();
    }

    public synchronized int consumir(){
        while (!lleno){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        lleno = false;
        System.out.println("consumido: " + contenido);
        notifyAll();
        return contenido;
    }

    static void main() {
        BufferManual bufferManual = new BufferManual();

        Thread productor = new Thread(()->{

            for (int i = 0; i < 10; i++) {
                bufferManual.producir(i);
            }
        });

        Thread consumidor = new Thread(()->{
            for (int i = 0; i < 10; i++) {
                bufferManual.consumir();
            }
        });

        productor.start();
        consumidor.start();
    }
}
