package org.example.modelo;

public class Jugador {
    private String nombre;
    private String simbolo;
    private int victorias = 0;
    private int derrotas = 0;
    private int empates = 0;

    public Jugador(String nombre, String simbolo) {
        this.nombre = nombre;
        this.simbolo = simbolo;
    }

    public String getNombre() { return nombre; }
    public String getSimbolo() { return simbolo; }
    public int getVictorias() { return victorias; }
    public void sumarVictoria() { this.victorias++; }
    public int getDerrotas() { return derrotas; }
    public void sumarDerrota() { this.derrotas++; }
    public int getEmpates() { return empates; }
    public void sumarEmpate() { this.empates++; }
}