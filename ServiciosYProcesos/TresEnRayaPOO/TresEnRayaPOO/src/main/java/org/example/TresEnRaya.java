package org.example;

import config.ThymeleafConstants;
import config.UrlConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@WebServlet("/tres")
public class TresEnRaya extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        // 1. Buscamos la partida en la sesión del usuario.
        // Si no existe o pide reiniciar, creamos una nueva exclusiva para él.
        Partida partida = (Partida) session.getAttribute("partida");
        if (partida == null || "true".equals(req.getParameter("reiniciar"))) {
            partida = new Partida();
            session.setAttribute("partida", partida);
        }

        String mensajeError = null;
        String coordenadaStr = req.getParameter("coordenada");

        // 2. Si el usuario ha indicado una coordenada, intentamos jugar
        if (coordenadaStr != null) {
            int coordenada = Integer.parseInt(coordenadaStr) - 1; // Convertimos de 1-9 a índice 0-8
            if (coordenada >= 0 && coordenada <= 8) {
                mensajeError = partida.jugar(coordenada); // Delegamos la lógica a la clase Partida
            }
        }

        // 3. Preparamos las filas dividiendo el array para pintarlas cómodamente
        String[] t = partida.getTablero();
        String fila1 = t[0] + " " + t[1] + " " + t[2];
        String fila2 = t[3] + " " + t[4] + " " + t[5];
        String fila3 = t[6] + " " + t[7] + " " + t[8];

        // 4. Empaquetamos todo para enviárselo a Thymeleaf
        var webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("fila1", fila1);
        ctx.setVariable("fila2", fila2);
        ctx.setVariable("fila3", fila3);
        ctx.setVariable("turno", partida.getTurno());
        ctx.setVariable("error", mensajeError);
        ctx.setVariable("ganador", partida.getGanador());
        ctx.setVariable("empate", partida.isEmpate());

        resp.setContentType(ThymeleafConstants.CONTENT_TYPE);
        ((TemplateEngine) getServletContext().getAttribute(ThymeleafConstants.TEMPLATE_ENGINE_ATTR))
                .process(UrlConstants.TEMPLATE_TRES_EN_RAYA, ctx, resp.getWriter());
    }
}