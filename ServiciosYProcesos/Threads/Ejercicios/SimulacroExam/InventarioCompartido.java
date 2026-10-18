package Threads.Ejercicios.SimulacroExam;
//
//nventario compartido — 30 minutos (2,5 puntos)
//Una tienda dispone de 100 unidades de un producto. Diez trabajadores procesan pedidos simultáneamente.
//        Requisitos:
//        1. Crea una clase Inventario con un atributo que represente las unidades disponibles.
//        2. Cada trabajador debe intentar vender 15 unidades, una por una.
//3. Si quedan unidades, la venta se realiza y el inventario disminuye.
//        4. Si no quedan unidades, se rechaza la venta.
//5. No se pueden vender unidades inexistentes ni permitir que el inventario sea negativo.
//6. Al terminar, muestra las unidades restantes, las ventas realizadas y las ventas rechazadas.
//        Utiliza synchronized o ReentrantLock para asegurar que la comprobación del stock y la venta se realizan de forma segura.
//        Salida final esperada:
//Unidades iniciales: 100
//Unidades vendidas: 100
//Unidades restantes: 0
//Ventas rechazadas: 50


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class InventarioCompartido {
    private int stock = 100;
    private final ReentrantLock candado = new ReentrantLock();
    private final AtomicInteger cantPerdidas = new AtomicInteger(0);
    private final AtomicInteger cantVendidas = new AtomicInteger(0);


    public int getStock() {
        return stock;
    }

    public AtomicInteger getCantPerdidas() {
        return cantPerdidas;
    }

    public int getCantVendidas() {
        return cantVendidas.get();
    }

    public void vender() {
        try {
            this.candado.lock();


            if (this.stock > 0) {
                this.stock--;
                cantVendidas.incrementAndGet();

            } else {
                cantPerdidas.incrementAndGet();
            }
        } finally {
            this.candado.unlock();
        }


    }

    static void main() {
        ExecutorService trabajadores = Executors.newFixedThreadPool(10);
        InventarioCompartido inventario = new InventarioCompartido();
        for (int i = 0; i < 10; i++) {
            trabajadores.submit(() -> {
                for (int j = 0; j < 15; j++) {
                    inventario.vender();
                }
            });
        }

        trabajadores.shutdown();

        try {
            trabajadores.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 4. Salida final esperada por el enunciado
        System.out.println("Unidades iniciales: 100");
        System.out.println("Unidades vendidas: " + inventario.getCantVendidas());
        System.out.println("Unidades restantes: " + inventario.getStock());
        System.out.println("Ventas rechazadas: " + inventario.getCantPerdidas());
    }


}

