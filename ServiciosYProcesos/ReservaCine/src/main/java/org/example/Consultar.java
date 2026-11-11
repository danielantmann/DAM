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

@WebServlet("/consultarReserva")
public class Consultar extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        ReservaCine reserva = (ReservaCine) session.getAttribute("reservaCine");

        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");

        if (reserva == null) {
            out.println("<h1>No hay ninguna sesión de cine configurada todavía.</h1>");
            out.println("</body></html>");
            out.close();
            return;
        }

        out.println("<h1>Cartelera: " + reserva.getPelicula() + "</h1>");
        out.println("<p><b>Hora de la sesión:</b> " + reserva.getHoraSesion() + "</p>");
        out.println("<p><b>Plazas totales:</b> " + reserva.getTotalPlazas() + "</p>");
        out.println("<p><b>Plazas ocupadas:</b> " + reserva.getPlazasOcupadas() + "</p>");
        out.println("<p><b>Plazas disponibles:</b> " + reserva.getPlazasDisponibles() + "</p>");

        out.println("<h3>Estado de los asientos (Libre: [ ], Ocupado: [X]):</h3>");
        out.println("<ul>");
        boolean[] asientos = reserva.getAsientos();
        for (int i = 0; i < asientos.length; i++) {
            String estado = asientos[i] ? "[X] OCUPADO" : "[ ] LIBRE";
            out.println("<li>Asiento " + (i + 1) + ": " + estado + "</li>");
        }
        out.println("</ul>");

        out.println("</body></html>");
        out.close();
    }
}