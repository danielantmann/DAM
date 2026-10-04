package Threads.Ejercicios.PracticaHilos.Ej02Banco;

import java.util.Random;

public class Cliente implements Runnable{
    private CuentaBancaria cuenta;
    private Random random = new Random();
    private final  int OPERACIONES = 10;
    public Cliente(CuentaBancaria cuenta) {
        this.cuenta = cuenta;
    }


    @Override
    public void run() {
        for (int i = 1; i <= OPERACIONES; i++) {
            int probabilidad = random.nextInt(11);
            int tiempo = random.nextInt(100,301);

            if ( probabilidad <= 6){
                cuenta.retirar(random.nextDouble(1,100));
            }else {
                cuenta.ingresar(random.nextDouble(1,50));
            }
            try {
                Thread.sleep(tiempo);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
