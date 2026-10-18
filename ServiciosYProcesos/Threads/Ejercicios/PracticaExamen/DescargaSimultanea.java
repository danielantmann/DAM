package Threads.Ejercicios.PracticaExamen;

public class DescargaSimultanea extends Thread{
    final  String name;

    public DescargaSimultanea(String name) {
        this.name = name;
    }

    public void run(){
        int progreso = 0;

        while (progreso < 100){
            System.out.println("Hilo: " + Thread.currentThread().getName() + " | Descarga: " + this.name + " sigue descargando (" + progreso + "%)");
            try {
                Thread.sleep(200);
                progreso += 25;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static void main() {
        for (int i = 1; i <= 4 ; i++) {
            DescargaSimultanea descarga = new DescargaSimultanea(String.valueOf(i));
            descarga.start();
        }

        System.out.println("todas las descargas finalizaron");
    }
}
