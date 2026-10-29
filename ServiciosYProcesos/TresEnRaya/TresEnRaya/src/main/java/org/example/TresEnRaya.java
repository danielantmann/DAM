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

        // 1. Obtener o inicializar el Tablero en la sesión
        String[] tablero = (String[]) session.getAttribute("tablero");
        if (tablero == null) {
            tablero = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
            session.setAttribute("tablero", tablero);
        }

        // 2. Obtener o inicializar el Turno ("X" empieza por defecto)
        String turnoActual = (String) session.getAttribute("turno");
        if (turnoActual == null) {
            turnoActual = "X";
            session.setAttribute("turno", turnoActual);
        }

        // Comprobar si ya hay un ganador o empate previo para no dejar seguir jugando
        String ganador = (String) session.getAttribute("ganador");
        Boolean empate = (Boolean) session.getAttribute("empate");
        if (ganador != null || (empate != null && empate)) {
            // Si el juego ya terminó, puedes reiniciarlo o simplemente mostrar el estado final
            // Aquí podemos permitir reiniciar si viene un parámetro ?reiniciar=true
            if ("true".equals(req.getParameter("reiniciar"))) {
                tablero = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
                session.setAttribute("tablero", tablero);
                session.setAttribute("turno", "X");
                session.removeAttribute("ganador");
                session.removeAttribute("empate");
                ganador = null;
                empate = false;
            }
        }

        String mensajeError = null;
        String coordenadaStr = req.getParameter("coordenada");

        if (coordenadaStr != null && ganador == null && (empate == null || !empate)) {
            int coordenada = Integer.parseInt(coordenadaStr) - 1;

            if (coordenada >= 0 && coordenada <= 8) {
                if (!tablero[coordenada].equals("-")) {
                    mensajeError = "¡Ese sitio ya está ocupado! Elige otra casilla.";
                } else {
                    // Colocar ficha
                    tablero[coordenada] = turnoActual;

                    // Comprobar si con esta jugada ha ganado
                    if (comprobarGanador(tablero, turnoActual)) {
                        ganador = turnoActual;
                        session.setAttribute("ganador", ganador);
                    } else if (comprobarEmpate(tablero)) {
                        empate = true;
                        session.setAttribute("empate", empate);
                    } else {
                        // Cambiar turno
                        turnoActual = turnoActual.equals("X") ? "O" : "X";
                        session.setAttribute("turno", turnoActual);
                    }
                }
            }
        }

        // Preparar las filas para Thymeleaf
        String fila1 = tablero[0] + " " + tablero[1] + " " + tablero[2];
        String fila2 = tablero[3] + " " + tablero[4] + " " + tablero[5];
        String fila3 = tablero[6] + " " + tablero[7] + " " + tablero[8];

        var webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("fila1", fila1);
        ctx.setVariable("fila2", fila2);
        ctx.setVariable("fila3", fila3);
        ctx.setVariable("turno", session.getAttribute("turno"));
        ctx.setVariable("error", mensajeError);
        ctx.setVariable("ganador", session.getAttribute("ganador"));
        ctx.setVariable("empate", session.getAttribute("empate"));

        resp.setContentType(ThymeleafConstants.CONTENT_TYPE);
        ((TemplateEngine) getServletContext().getAttribute(ThymeleafConstants.TEMPLATE_ENGINE_ATTR))
                .process(UrlConstants.TEMPLATE_TRES_EN_RAYA, ctx, resp.getWriter());
    }

    // Método para comprobar si un jugador ha hecho 3 en raya
    private boolean comprobarGanador(String[] t, String m) {
        // Horizontales
        if (t[0].equals(m) && t[1].equals(m) && t[2].equals(m)) return true;
        if (t[3].equals(m) && t[4].equals(m) && t[5].equals(m)) return true;
        if (t[6].equals(m) && t[7].equals(m) && t[8].equals(m)) return true;
        // Verticales
        if (t[0].equals(m) && t[3].equals(m) && t[6].equals(m)) return true;
        if (t[1].equals(m) && t[4].equals(m) && t[7].equals(m)) return true;
        if (t[2].equals(m) && t[5].equals(m) && t[8].equals(m)) return true;
        // Diagonales
        if (t[0].equals(m) && t[4].equals(m) && t[8].equals(m)) return true;
        if (t[2].equals(m) && t[4].equals(m) && t[6].equals(m)) return true;

        return false;
    }

    // Método para comprobar si hay empate (tablero lleno sin ganador)
    private boolean comprobarEmpate(String[] t) {
        for (String celda : t) {
            if (celda.equals("-")) {
                return false; // Todavía hay huecos libres
            }
        }
        return true; // No hay huecos y nadie ganó
    }
}