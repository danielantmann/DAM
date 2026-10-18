package Threads.Ejercicios.practicaExam2;

import java.util.concurrent.atomic.AtomicInteger;

public class ContadorVisitas {
    private int visitas = 0;
    private int visitasSync = 0;
    private AtomicInteger visitasAtomic = new AtomicInteger(0);

    public ContadorVisitas(int visitas, int visitasSync) {
        this.visitas = visitas;
        this.visitasSync = visitasSync;

    }

    public int getVisitas() {
        return visitas;
    }

    public int getVisitasSync() {
        return visitasSync;
    }

    public AtomicInteger getVisitasAtomic() {
        return visitasAtomic;
    }

    public void incrementarVisitas(){
        this.visitas ++;
    }

    public synchronized  void incrementarVisitasSync(){
        this.visitasSync ++;
    }

    static void main() {
        ContadorVisitas c = new ContadorVisitas(0,0);
        Thread[] v  = new Thread[1000];
        for (int i = 0; i < 1000 ; i++) {
            v[i] = new Thread(()->{
               c.incrementarVisitas();

               c.incrementarVisitasSync();

               c.getVisitasAtomic().incrementAndGet();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        v[i].start();
        }


        for (int i = 0; i <v.length ; i++) {
            try {
                v[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Sin sinc, visitas contadas: " + c.getVisitas());
        System.out.println("Con sync, visitas: " + c.getVisitasSync());
        System.out.println("con atomic: " +c.getVisitasAtomic().get());
    }


}
