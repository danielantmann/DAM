package Threads.Ejercicios.PracticaHilos.Ej02Banco;

public class BancoMainRefactored {
    public static void main(String[] args) {
        CuentaBancaria cuentaSync = new CuentaConSync(10000);
        CuentaBancaria cuentaRentran = new CuentaConReentranLock(10000);
        CuentaBancaria cuentaVolatil = new CuentaVolatile(10000);
        final int CLIENTES = 50;

        long tiempoRentran = simularOperacion(cuentaRentran,CLIENTES);
        long tiempoSync = simularOperacion(cuentaSync,CLIENTES);
        long tiempoVolatil = simularOperacion(cuentaVolatil, CLIENTES);

        System.out.println("=== BANCO VIRTUAL ===");
        System.out.println("Saldo inicial: 10000.00€");
        System.out.println("50 clientes realizando 500 operaciones totales...");
        System.out.println("");

        pintarResultados(cuentaRentran,tiempoRentran);
        pintarResultados(cuentaSync,tiempoSync);
        pintarResultados(cuentaVolatil,tiempoVolatil);
    }

    public static long simularOperacion(CuentaBancaria cuenta , int clientes){
        Thread[] hilos = new Thread[clientes];

        long tiempoIncio = System.currentTimeMillis();

        for (int i = 0; i < clientes; i++) {
            Cliente cliente = new Cliente(cuenta);
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
        return (System.currentTimeMillis() - tiempoIncio) / 100;
    }

    public static void pintarResultados(CuentaBancaria cuenta ,long tiempoS ){
        String nombre = cuenta.getClass().getSimpleName();
        System.out.println("--- CON " + nombre + " ---");

        if (cuenta.getOperacionesFallidas().get() + cuenta.getOperacionesExitosas().get() !=500 ){
            System.out.printf("Saldo final: %.2f€ ❌ INCORRECTO\n", cuenta.consultarSaldo());
            System.out.println("Operaciones perdidas detectadas!");}
        else {
            System.out.printf("Saldo final: %.2f€\n", cuenta.consultarSaldo());
            System.out.println("Operaciones exitosas: " + cuenta.getOperacionesExitosas().get()+ "/500" );
            System.out.println("Operaciones fallidas: " + cuenta.getOperacionesFallidas().get()+ " fondos insuficientes" );
            System.out.println("Tiempo total: " + tiempoS+ "s");
        }


    }
}
