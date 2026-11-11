package org.example.modelo;

import java.util.ArrayList;
import java.util.List;

public class Partida {
    private static int contadorId = 1;
    private int id;
    private Jugador jugador1;
    private Jugador jugador2;
    private String ganadorNombre; // o "Empate"
    private int movimientosTotales;
    private List<String> historialMovimientos = new ArrayList<>();

    public Partida(Jugador j1, Jugador j2) {
        this.id = contadorId++;
        this.jugador1 = j1;
        this.jugador2 = j2;
    }

    public int getId() { return id; }
    public Jugador getJugador1() { return jugador1; }
    public Jugador getJugador2() { return jugador2; }
    public String getGanadorNombre() { return ganadorNombre; }
    public void setGanadorNombre(String ganadorNombre) { this.ganadorNombre = ganadorNombre; }
    public int getMovimientosTotales() { return movimientosTotales; }
    public void setMovimientosTotales(int movimientosTotales) { this.movimientosTotales = movimientosTotales; }
    public List<String> getHistorialMovimientos() { return historialMovimientos; }

    public void agregarMovimiento(String desc) {
        historialMovimientos.add(desc);
        movimientosTotales++;
    }
}