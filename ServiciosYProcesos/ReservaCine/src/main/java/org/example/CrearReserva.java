package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.modelo.ReservaCine;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/crearReserva")
public class CrearReserva extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pelicula = req.getParameter("pelicula");
        String hora = req.getParameter("sesion");
        String plazasStr = req.getParameter("plazas");

        if (pelicula == null || hora == null || plazasStr == null || pelicula.isEmpty() || hora.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros obligatorios.");
            return;
        }

        int plazas;
        try {
            plazas = Integer.parseInt(plazasStr);
            if (plazas <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Las plazas deben ser un número positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El formato de las plazas no es válido.");
            return;
        }

        // Creamos la reserva y la guardamos en la sesión del usuario
        ReservaCine reserva = new ReservaCine(pelicula, hora, plazas);
        HttpSession session = req.getSession();
        session.setAttribute("reservaCine", reserva);

        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Reserva de cine configurada con éxito</h1>");
        out.println("<p>Película: " + pelicula + " (" + hora + ") - Plazas: " + plazas + "</p>");
        out.println("<a href='consultarReserva'>Ver estado de la sala</a>");
        out.close();
    }
}