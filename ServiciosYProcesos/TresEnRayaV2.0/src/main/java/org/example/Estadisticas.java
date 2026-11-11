package org.example;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.modelo.Jugador;
import org.example.modelo.Partida;
import org.example.modelo.Torneo;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/estadisticas")
public class Estadisticas extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext contexto = getServletContext();
        Torneo torneo = (Torneo) contexto.getAttribute("torneoGlobal");

        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");

        if (torneo == null) {
            out.println("<h1>No hay datos en el torneo todavía.</h1></body></html>");
            out.close();
            return;
        }

        String nombreBuscado = req.getParameter("nombre");
        String idPartidaStr = req.getParameter("partida");

        if (idPartidaStr != null) {
            // Opción 3: Historial de movimientos de una partida concreta
            int id = Integer.parseInt(idPartidaStr);
            Partida p = torneo.getPartidas().stream().filter(part -> part.getId() == id).findFirst().orElse(null);
            out.println("<h1>Historial de movimientos de la Partida #" + id + "</h1>");
            if (p != null) {
                out.println("<ul>");
                for (String mov : p.getHistorialMovimientos()) {
                    out.println("<li>" + mov + "</li>");
                }
                out.println("</ul>");
            } else {
                out.println("<p>Partida no encontrada.</p>");
            }
        } else if (nombreBuscado != null) {
            // Opción 2: Partidas de un jugador específico
            out.println("<h1>Partidas del jugador: " + nombreBuscado + "</h1><ul>");
            for (Partida p : torneo.getPartidas()) {
                if (p.getJugador1().getNombre().equals(nombreBuscado) || p.getJugador2().getNombre().equals(nombreBuscado)) {
                    boolean esJ1 = p.getJugador1().getNombre().equals(nombreBuscado);
                    String simbolo = esJ1 ? p.getJugador1().getSimbolo() : p.getJugador2().getSimbolo();
                    String rival = esJ1 ? p.getJugador2().getNombre() : p.getJugador1().getNombre();
                    out.println("<li>Partida #" + p.getId() + " | Símbolo: " + simbolo + " | Rival: " + rival + " | Resultado: " + p.getGanadorNombre() + "</li>");
                }
            }
            out.println("</ul>");
        } else {
            // Opción 1: Victorias globales de cada jugador (Sin parámetros)
            out.println("<h1>Victorias de los Jugadores</h1><ul>");
            for (Jugador j : torneo.getJugadores()) {
                out.println("<li><b>" + j.getNombre() + "</b> - Victorias: " + j.getVictorias() + " | Derrotas: " + j.getDerrotas() + " | Empates: " + j.getEmpates() + "</li>");
            }
            out.println("</ul>");
        }

        out.println("</body></html>");
        out.close();
    }
}