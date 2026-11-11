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

@WebServlet("/cancelarAsiento")
public class Cancelar extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        ReservaCine reserva = (ReservaCine) session.getAttribute("reservaCine");

        if (reserva == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No hay ninguna reserva activa.");
            return;
        }

        String asientoStr = req.getParameter("asiento");
        if (asientoStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro asiento.");
            return;
        }

        int numeroAsiento = Integer.parseInt(asientoStr);
        int indice = numeroAsiento - 1;

        if (indice < 0 || indice >= reserva.getTotalPlazas()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Asiento inexistente.");
            return;
        }

        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");

        if (!reserva.getAsientos()[indice]) {
            out.println("<h2>El asiento " + numeroAsiento + " ya estaba libre.</h2>");
        } else {
            reserva.getAsientos()[indice] = false; // Liberamos el asiento
            out.println("<h2>Asiento " + numeroAsiento + " cancelado correctamente.</h2>");
        }

        out.println("<br><a href='consultarReserva'>Volver al estado de la sala</a>");
        out.println("</body></html>");
        out.close();
    }
}