package org.example.modelo;

import java.util.ArrayList;
import java.util.List;

public class JuegoAhorcado {
    private String palabraSecreta;
    private char[] estadoProgreso; // Array para ir mostrando '_' y letras acertadas
    private int intentosRestantes;
    private List<Character> letrasProbadas;
    private boolean ganado;
    private boolean perdido;

    public JuegoAhorcado(String palabraSecreta, int intentosMaximos) {
        this.palabraSecreta = palabraSecreta.toLowerCase();
        this.intentosRestantes = intentosMaximos;
        this.letrasProbadas = new ArrayList<>();
        this.ganado = false;
        this.perdido = false;

        // Inicializamos el progreso con guiones bajos '_' por cada letra
        this.estadoProgreso = new char[this.palabraSecreta.length()];
        for (int i = 0; i < this.estadoProgreso.length; i++) {
            this.estadoProgreso[i] = '_';
        }
    }

    public String getPalabraSecreta() {
        return palabraSecreta;
    }

    public char[] getEstadoProgreso() {
        return estadoProgreso;
    }

    public int getIntentosRestantes() {
        return intentosRestantes;
    }

    public List<Character> getLetrasProbadas() {
        return letrasProbadas;
    }

    public boolean isGanado() {
        return ganado;
    }

    public boolean isPerdido() {
        return perdido;
    }

    // Método principal para procesar la letra que introduce el usuario
    public void arriesgarLetra(char letra) {
        letra = Character.toLowerCase(letra);

        // Si ya la había probado o el juego ya terminó, no hacemos nada
        if (letrasProbadas.contains(letra) || ganado || perdido) {
            return;
        }

        letrasProbadas.add(letra);

        boolean acierto = false;
        for (int i = 0; i < palabraSecreta.length(); i++) {
            if (palabraSecreta.charAt(i) == letra) {
                estadoProgreso[i] = letra;
                acierto = true;
            }
        }

        if (!acierto) {
            intentosRestantes--;
            if (intentosRestantes <= 0) {
                perdido = true;
            }
        }

        // Comprobamos si ya no quedan guiones bajos (¡Partida ganada!)
        boolean faltaAlguUnGuion = false;
        for (char c : estadoProgreso) {
            if (c == '_') {
                faltaAlguUnGuion = true;
                break;
            }
        }
        if (!faltaAlguUnGuion) {
            ganado = true;
        }
    }
}