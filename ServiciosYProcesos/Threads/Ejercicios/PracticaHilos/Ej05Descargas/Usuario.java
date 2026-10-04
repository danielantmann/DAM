package Threads.Ejercicios.PracticaHilos.Ej05Descargas;

public class Usuario {
    private final String nombre;
    private final boolean premium;

    public Usuario(String nombre, boolean premium) {
        this.nombre = nombre;
        this.premium = premium;
    }

    public String getNombre() { return nombre; }
    public boolean isPremium() { return premium; }
}