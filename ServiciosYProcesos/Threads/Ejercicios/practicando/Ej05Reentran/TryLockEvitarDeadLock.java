package Threads.Ejercicios.practicando.Ej05Reentran;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TryLockEvitarDeadLock {
    private final ReentrantLock candado1 = new ReentrantLock();
    private final ReentrantLock candado2 = new ReentrantLock();

    public void intentar(ReentrantLock primero, ReentrantLock segundo) {
        boolean conseguido = false;

        while (!conseguido) {
            try {
                if (primero.tryLock(500, TimeUnit.MILLISECONDS)) {
                    try {
                        System.out.println(Thread.currentThread().getName() + ": Primer candado conseguido");

                        if (segundo.tryLock(500, TimeUnit.MILLISECONDS)) {
                            try {
                                System.out.println(Thread.currentThread().getName() + ": ¡Segundo candado conseguido! Trabajo hecho.");
                                conseguido = true;
                            } finally {
                                segundo.unlock();
                            }
                        } else {
                            System.out.println(Thread.currentThread().getName() + ": No se consiguió el segundo. Soltando el primero y reintentando...");
                        }
                    } finally {
                        primero.unlock();
                    }
                } else {
                    System.out.println(Thread.currentThread().getName() + ": No se consiguió el primero, reintentando...");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (!conseguido) {
                try {
                    Thread.sleep((long) (Math.random() * 200));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static void main() {
        TryLockEvitarDeadLock sis = new TryLockEvitarDeadLock();

      Thread hilo1 = new Thread(()->{
         sis.intentar(sis.candado1,sis.candado2);
      });

        Thread hilo2 = new Thread(()->{
            sis.intentar(sis.candado2,sis.candado1);
        });

    hilo1.start();
    hilo2.start();
    }

}
