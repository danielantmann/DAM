package org.example.modelo;

public class ReservaCine {
    private String pelicula;
    private String horaSesion;
    private int totalPlazas;
    private boolean[] asientos; // false = libre, true = ocupado

    public ReservaCine(String pelicula, String horaSesion, int totalPlazas) {
        this.pelicula = pelicula;
        this.horaSesion = horaSesion;
        this.totalPlazas = totalPlazas;
        this.asientos = new boolean[totalPlazas]; // Todos empiezan a false (libres)
    }

    public String getPelicula() { return pelicula; }
    public String getHoraSesion() { return horaSesion; }
    public int getTotalPlazas() { return totalPlazas; }
    public boolean[] getAsientos() { return asientos; }

    public int getPlazasOcupadas() {
        int count = 0;
        for (boolean ocupado : asientos) {
            if (ocupado) count++;
        }
        return count;
    }

    public int getPlazasDisponibles() {
        return totalPlazas - getPlazasOcupadas();
    }
}