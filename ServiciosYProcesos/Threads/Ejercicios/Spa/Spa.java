package Threads.Ejercicios.Spa;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Spa {
    private final int CLIENTES = 7;
    private final int HIDROMASAJE = 2;
    private final AtomicInteger contador = new AtomicInteger(0);
    private final AtomicInteger caja = new AtomicInteger(0);

    Semaphore hidro = new Semaphore(HIDROMASAJE, true);
    ArrayList<Cliente>  listaCliente =  new ArrayList<>();

    public void ejecutarDemo(){
        for (int i = 1; i <= CLIENTES ; i++) {
            Cliente cliente = new Cliente("cliente "+ i ,hidro,contador,caja);
            listaCliente.add(cliente);
            cliente.start();
        }

        for (Cliente cliente : listaCliente){
            try {
                cliente.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Clientes: " + contador.get());
        System.out.println("Total Caja: " + caja.get());
    }
}
