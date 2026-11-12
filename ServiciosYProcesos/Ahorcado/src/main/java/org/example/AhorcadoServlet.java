package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.modelo.JuegoAhorcado;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/ahorcado")
public class AhorcadoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        // Recuperamos o creamos el juego en la sesión del usuario
        JuegoAhorcado juego = (JuegoAhorcado) session.getAttribute("juegoAhorcado");

        String reiniciar = req.getParameter("reiniciar");
        if (juego == null || "true".equals(reiniciar)) {
            // Palabra fija para pruebas (o se podría recibir por parámetro)
            juego = new JuegoAhorcado("servlets", 6);
            session.setAttribute("juegoAhorcado", juego);
        }

        // Si el usuario ha enviado una letra a través de un formulario o parámetro
        String letraStr = req.getParameter("letra");
        if (letraStr != null && !letraStr.isEmpty() && !juego.isGanado() && !juego.isPerdido()) {
            char letra = letraStr.charAt(0);
            juego.arriesgarLetra(letra);
        }

        // Renderizamos la respuesta HTML
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><head><title>Juego del Ahorcado</title></head><body>");
        out.println("<h1>Juego del Ahorcado (Servlets y Sesiones)</h1>");

        if (juego.isGanado()) {
            out.println("<h2 style='color: green;'>¡Felicidades! Has ganado la partida. La palabra era: " + juego.getPalabraSecreta() + "</h2>");
        } else if (juego.isPerdido()) {
            out.println("<h2 style='color: red;'>¡Has perdido! Te has quedado sin intentos. La palabra era: " + juego.getPalabraSecreta() + "</h2>");
        } else {
            // Mostramos el progreso separado por espacios (ej: _ e r v _ l e t s)
            out.println("<h3>Palabra: <span style='font-size: 24px; letter-spacing: 5px;'>");
            for (char c : juego.getEstadoProgreso()) {
                out.print(c + " ");
            }
            out.println("</span></h3>");

            out.println("<p><b>Intentos restantes:</b> " + juego.getIntentosRestantes() + "</p>");
            out.println("<p><b>Letras probadas:</b> " + juego.getLetrasProbadas() + "</p>");

            // Formulario sencillo para enviar la siguiente letra
            out.println("<form action='ahorcado' method='GET'>");
            out.println("Introduce una letra: <input type='text' name='letra' maxlength='1' required>");
            out.println("<input type='submit' value='Probar'>");
            out.println("</form>");
        }

        out.println("<br><br><a href='ahorcado?reiniciar=true'>Reiniciar Partida</a>");
        out.println("</body></html>");
        out.close();
    }
}