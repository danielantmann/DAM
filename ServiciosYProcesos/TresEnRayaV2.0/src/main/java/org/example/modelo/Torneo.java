package org.example.modelo;

import java.util.ArrayList;
import java.util.List;

public class Torneo {
    private List<Jugador> jugadores = new ArrayList<>();
    private List<Partida> partidas = new ArrayList<>();

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public List<Partida> getPartidas() {
        return partidas;
    }

    public Jugador buscarJugador(String nombre) {
        return jugadores.stream()
                .filter(j -> j.getNombre().equals(nombre))
                .findFirst()
                .orElse(null);
    }
}