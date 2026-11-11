package org.example;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.modelo.Jugador;
import org.example.modelo.Torneo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/finTorneo")
public class FinTorneo extends HttpServlet {
    private static int llamadaPodio = 0;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext contexto = getServletContext();
        Torneo torneo = (Torneo) contexto.getAttribute("torneoGlobal");

        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");

        if (torneo == null || torneo.getJugadores().isEmpty()) {
            out.println("<h1>No hay jugadores registrados en el torneo.</h1></body></html>");
            out.close();
            return;
        }

        List<Jugador> jugadores = torneo.getJugadores();
        jugadores.sort((j1, j2) -> Integer.compare(j2.getVictorias(), j1.getVictorias()));

        String titulo = "";
        Jugador j = null;

        if (llamadaPodio == 0) {
            titulo = "🥉 TERCER PUESTO";
            j = jugadores.size() > 2 ? jugadores.get(2) : null;
        } else if (llamadaPodio == 1) {
            titulo = "🥈 SUBCAMPEÓN";
            j = jugadores.size() > 1 ? jugadores.get(1) : null;
        } else if (llamadaPodio == 2) {
            titulo = "🏆 ¡CAMPEÓN DEL TORNEO!";
            j = jugadores.get(0);
        }

        out.println("<h1>" + titulo + "</h1>");
        if (j != null) {
            out.println("<p>Jugador: <b>" + j.getNombre() + "</b> con " + j.getVictorias() + " victorias.</p>");
        } else {
            out.println("<p>No hay suficientes participantes para este puesto.</p>");
        }

        out.println("</body></html>");

        // Rotar el contador en ciclo (0 -> 1 -> 2 -> 0)
        llamadaPodio = (llamadaPodio + 1) % 3;
        out.close();
    }
}