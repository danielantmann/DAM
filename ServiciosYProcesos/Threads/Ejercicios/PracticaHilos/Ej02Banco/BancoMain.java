package Threads.Ejercicios.PracticaHilos.Ej02Banco;

public class BancoMain {
    public static void main(String[] args) {
        CuentaBancaria cuentaSync = new CuentaConSync(10000);
        CuentaBancaria cuentaRentran = new CuentaConReentranLock(10000);
        CuentaBancaria cuentaVolatil = new CuentaVolatile(10000);
        final int CLIENTES = 50;


        Thread[] hilos = new Thread[CLIENTES];

        long inicioRentran = System.currentTimeMillis();
        for (int i = 0; i < CLIENTES; i++) {
            Cliente cliente = new Cliente(cuentaRentran);
            hilos[i] = new Thread(cliente);
            hilos[i].start();
        }

        for (Thread hilo : hilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long tiempoRentran = (System.currentTimeMillis() - inicioRentran) / 100;

        long inicioSync = System.currentTimeMillis();
        for (int i = 0; i < CLIENTES; i++) {
            Cliente cliente = new Cliente(cuentaSync);
            hilos[i] = new Thread(cliente);
            hilos[i].start();
        }

        for (Thread hilo : hilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long tiempoSync = (System.currentTimeMillis() - inicioSync) / 100;


        long inicioVolatil = (System.currentTimeMillis());

        for (int i = 0; i < 50 ; i++) {
            Cliente cliente = new Cliente(cuentaVolatil);
            hilos[i] = new Thread(cliente);
            hilos[i].start();
        }

        for (Thread hilo : hilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        long tiempoVolatil = System.currentTimeMillis() - inicioVolatil;

        System.out.println("=== BANCO VIRTUAL ===");
        System.out.println("Saldo inicial: 10000.00€");
        System.out.println("50 clientes realizando 500 operaciones totales...");
        System.out.println("");
        System.out.println("--- CON REENTRANTLOCK ---");
        System.out.printf("Saldo final: %.2f€\n", cuentaRentran.consultarSaldo());
        System.out.println("Operaciones exitosas: " + cuentaRentran.getOperacionesExitosas().get()+ "/500" );
        System.out.println("Operaciones fallidas: " + cuentaRentran.getOperacionesFallidas().get()+ " fondos insuficientes" );
        System.out.println("Tiempo total: " + tiempoRentran+ "s");
        System.out.println("");
        System.out.println("--- CON SYNCHRONIZED ---");
        System.out.printf("Saldo final: %.2f€\n", cuentaSync.consultarSaldo());
        System.out.println("Operaciones exitosas: " + cuentaSync.getOperacionesExitosas().get()+ "/500" );
        System.out.println("Operaciones fallidas: " + cuentaSync.getOperacionesFallidas().get()+ " fondos insuficientes" );
        System.out.println("Tiempo total: " + tiempoSync+ "s");
        System.out.println("");
        System.out.println("--- CON VOLATIL ---");

        if (cuentaVolatil.getOperacionesFallidas().get() + cuentaVolatil.getOperacionesExitosas().get() !=500 ){
            System.out.printf("Saldo final: %.2f€ ❌ INCORRECTO\n", cuentaVolatil.consultarSaldo());
            System.out.println("Operaciones perdidas detectadas!");}
        else {
            System.out.printf("Saldo final: %.2f€\n", cuentaVolatil.consultarSaldo());
            System.out.println("Operaciones exitosas: " + cuentaVolatil.getOperacionesExitosas().get()+ "/500" );
            System.out.println("Operaciones fallidas: " + cuentaVolatil.getOperacionesFallidas().get()+ " fondos insuficientes" );
            System.out.println("Tiempo total: " + tiempoSync+ "s");
        }


    }
}
