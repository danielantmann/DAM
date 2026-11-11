package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.modelo.Jugador;
import org.example.modelo.Partida;
import org.example.modelo.Torneo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

@WebServlet("/iniciarPartida")
public class IniciarPartida extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String n1 = req.getParameter("jugador1");
        String s1 = req.getParameter("simbolo1");
        String n2 = req.getParameter("jugador2");
        String s2 = req.getParameter("simbolo2");

        if (n1 == null || s1 == null || n2 == null || s2 == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros");
            return;
        }

        HttpSession session = req.getSession();

        // CORRECCIÓN PROFESOR: El Torneo se guarda en la SESIÓN, no en el contexto global
        Torneo torneo = (Torneo) session.getAttribute("torneo");
        if (torneo == null) {
            torneo = new Torneo();
            session.setAttribute("torneo", torneo);
        }

        Jugador j1 = torneo.buscarJugador(n1);
        if (j1 == null) { j1 = new Jugador(n1, s1); torneo.getJugadores().add(j1); }

        Jugador j2 = torneo.buscarJugador(n2);
        if (j2 == null) { j2 = new Jugador(n2, s2); torneo.getJugadores().add(j2); }

        Partida partidaActual = new Partida(j1, j2);
        torneo.getPartidas().add(partidaActual);

        Random rand = new Random();
        Jugador turnoInicial = rand.nextBoolean() ? j1 : j2;

        session.setAttribute("partidaActual", partidaActual);
        session.setAttribute("jugadorTurno", turnoInicial);
        session.setAttribute("tablero", new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"});
        session.removeAttribute("ganador");
        session.removeAttribute("empate");

        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Partida iniciada para el torneo</h1>");
        out.println("<p>Turno inicial para: <b>" + turnoInicial.getNombre() + " (" + turnoInicial.getSimbolo() + ")</b></p>");
        out.println("<a href='tres'>Ir al tablero</a>");
        out.close();
    }
}