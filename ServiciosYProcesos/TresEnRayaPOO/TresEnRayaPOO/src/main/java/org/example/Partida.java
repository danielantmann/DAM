package org.example;

public class Partida {
    private String[] tablero;
    private String turno;
    private String ganador;
    private boolean empate;

    public Partida() {
        // Inicializamos el tablero vacío con 9 guiones y el turno de la 'X'
        this.tablero = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
        this.turno = "X";
        this.ganador = null;
        this.empate = false;
    }

    // Método principal para realizar una jugada
    public String jugar(int coordenada) {
        if (ganador != null || empate) {
            return "La partida ya ha terminado.";
        }
        if (!tablero[coordenada].equals("-")) {
            return "¡Ese sitio ya está ocupado! Elige otra casilla.";
        }

        // Colocamos la ficha del turno actual
        tablero[coordenada] = turno;

        // Comprobamos si con esta jugada ha ganado o empatado
        if (comprobarGanador(turno)) {
            ganador = turno;
        } else if (comprobarEmpate()) {
            empate = true;
        } else {
            // Si nadie gana ni empata, cambiamos de turno
            turno = turno.equals("X") ? "O" : "X";
        }
        return null; // Si todo va bien, devuelve null (sin errores)
    }

    private boolean comprobarGanador(String m) {
        // Líneas horizontales, verticales y diagonales
        if (tablero[0].equals(m) && tablero[1].equals(m) && tablero[2].equals(m)) return true;
        if (tablero[3].equals(m) && tablero[4].equals(m) && tablero[5].equals(m)) return true;
        if (tablero[6].equals(m) && tablero[7].equals(m) && tablero[8].equals(m)) return true;
        if (tablero[0].equals(m) && tablero[3].equals(m) && tablero[6].equals(m)) return true;
        if (tablero[1].equals(m) && tablero[4].equals(m) && tablero[7].equals(m)) return true;
        if (tablero[2].equals(m) && tablero[5].equals(m) && tablero[8].equals(m)) return true;
        if (tablero[0].equals(m) && tablero[4].equals(m) && tablero[8].equals(m)) return true;
        if (tablero[2].equals(m) && tablero[4].equals(m) && tablero[6].equals(m)) return true;
        return false;
    }

    private boolean comprobarEmpate() {
        for (String celda : tablero) {
            if (celda.equals("-")) return false; // Si queda algún guión, no hay empate
        }
        return true;
    }

    // Getters para que Thymeleaf pueda consultar los datos fácilmente
    public String[] getTablero() { return tablero; }
    public String getTurno() { return turno; }
    public String getGanador() { return ganador; }
    public boolean isEmpate() { return empate; }
}