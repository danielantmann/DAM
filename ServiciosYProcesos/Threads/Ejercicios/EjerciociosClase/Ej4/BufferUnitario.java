package Threads.Ejercicios.EjerciociosClase.Ej4;

//Ejercicio 4.1 — Productor-consumidor manual
//Implementa un buffer de capacidad 1 (una casilla) compartido entre un hilo Productor y
// un hilo Consumidor:
//
//El Productor debe esperar (wait()) si el buffer está lleno.
//El Consumidor debe esperar (wait()) si el buffer está vacío.
//        Usa notifyAll() para despertar al hilo correspondiente.

public class BufferUnitario {
    private Integer valor = null;

    public synchronized void producir(int v) throws InterruptedException {
        while (valor != null){
            wait();
        }
        valor =v;
        System.out.println("Producido: " + v);
        notifyAll();
    }

    public synchronized int consumir() throws InterruptedException {
        while (valor == null){
            wait();
        }
        int v = valor;
        valor = null;
        System.out.println("consumido: " + v);
        notifyAll();
        return v;
    }

    public static void main(String[] args) {
        BufferUnitario buffer = new BufferUnitario();

        Thread productor = new Thread(()->{
            for (int i = 1; i <= 5 ; i++) {
                try {
                    buffer.producir(i);
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread consumidor = new Thread(()->{
            for (int i = 1; i <=5 ; i++) {
                try {
                    buffer.consumir();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        productor.start();
        consumidor.start();
    }
}
