package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.modelo.Torneo;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/borrarJugador")
public class BorrarJugador extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nombre = req.getParameter("nombre");
        if (nombre == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro nombre");
            return;
        }

        HttpSession session = req.getSession();
        Torneo torneo = (Torneo) session.getAttribute("torneo");

        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");

        if (torneo == null) {
            out.println("<h1>No hay ningún torneo activo en la sesión.</h1></body></html>");
            out.close();
            return;
        }

        torneo.getJugadores().removeIf(j -> j.getNombre().equals(nombre));
        torneo.getPartidas().removeIf(p -> p.getJugador1().getNombre().equals(nombre) || p.getJugador2().getNombre().equals(nombre));

        out.println("<h1>Jugador " + nombre + " borrado del torneo y sus partidas eliminadas.</h1>");
        out.println("</body></html>");
        out.close();
    }
}