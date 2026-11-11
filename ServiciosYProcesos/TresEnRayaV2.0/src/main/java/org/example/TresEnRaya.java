package org.example;

import config.ThymeleafConstants;
import config.UrlConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.modelo.Jugador;
import org.example.modelo.Partida;
import org.example.modelo.Torneo;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.Arrays;

@WebServlet("/tres")
public class TresEnRaya extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        // CORRECCIÓN PROFESOR: Validar que el torneo y la partida existan por si entran directo por URL
        Torneo torneo = (Torneo) session.getAttribute("torneo");
        Partida partidaActual = (Partida) session.getAttribute("partidaActual");

        if (torneo == null || partidaActual == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No hay ningún torneo o partida activa. Inicia sesión primero.");
            return;
        }

        Jugador j1 = partidaActual.getJugador1();
        Jugador j2 = partidaActual.getJugador2();

        String[] tablero = (String[]) session.getAttribute("tablero");
        if (tablero == null) {
            tablero = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
            session.setAttribute("tablero", tablero);
        }

        Jugador jugadorTurno = (Jugador) session.getAttribute("jugadorTurno");
        if (jugadorTurno == null) { jugadorTurno = j1; }

        String ganadorNombre = (String) session.getAttribute("ganador");
        Boolean empate = (Boolean) session.getAttribute("empate");

        if ("true".equals(req.getParameter("reiniciar"))) {
            tablero = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
            session.setAttribute("tablero", tablero);
            session.removeAttribute("ganador");
            session.removeAttribute("empate");
            ganadorNombre = null;
            empate = false;
        }

        String mensajeError = null;
        String coordenadaStr = req.getParameter("coordenada");

        if (coordenadaStr != null && ganadorNombre == null && (empate == null || !empate)) {
            int coordenada = Integer.parseInt(coordenadaStr) - 1;
            if (coordenada >= 0 && coordenada <= 8) {
                if (!tablero[coordenada].equals("-")) {
                    mensajeError = "¡Casilla ocupada!";
                } else {
                    String simbolo = jugadorTurno.getSimbolo();
                    tablero[coordenada] = simbolo;
                    partidaActual.agregarMovimiento(jugadorTurno.getNombre() + " (" + simbolo + ") -> casilla " + (coordenada + 1));

                    if (comprobarGanador(tablero, simbolo)) {
                        ganadorNombre = jugadorTurno.getNombre();
                        session.setAttribute("ganador", ganadorNombre);
                        partidaActual.setGanadorNombre(ganadorNombre);
                        jugadorTurno.sumarVictoria();
                        Jugador perdedor = jugadorTurno.equals(j1) ? j2 : j1;
                        perdedor.sumarDerrota();
                    } else if (comprobarEmpate(tablero)) {
                        empate = true;
                        session.setAttribute("empate", empate);
                        partidaActual.setGanadorNombre("Empate");
                        j1.sumarEmpate();
                        j2.sumarEmpate();
                    } else {
                        jugadorTurno = jugadorTurno.equals(j1) ? j2 : j1;
                        session.setAttribute("jugadorTurno", jugadorTurno);
                    }
                }
            }
        }

        String fila1 = tablero[0] + " " + tablero[1] + " " + tablero[2];
        String fila2 = tablero[3] + " " + tablero[4] + " " + tablero[5];
        String fila3 = tablero[6] + " " + tablero[7] + " " + tablero[8];

        var webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("fila1", fila1);
        ctx.setVariable("fila2", fila2);
        ctx.setVariable("fila3", fila3);
        ctx.setVariable("turno", jugadorTurno.getNombre() + " (" + jugadorTurno.getSimbolo() + ")");
        ctx.setVariable("error", mensajeError);
        ctx.setVariable("ganador", session.getAttribute("ganador"));
        ctx.setVariable("empate", session.getAttribute("empate"));

        resp.setContentType(ThymeleafConstants.CONTENT_TYPE);
        ((TemplateEngine) getServletContext().getAttribute(ThymeleafConstants.TEMPLATE_ENGINE_ATTR))
                .process(UrlConstants.TEMPLATE_TRES_EN_RAYA, ctx, resp.getWriter());
    }

    private boolean comprobarGanador(String[] t, String m) {
        if (t[0].equals(m) && t[1].equals(m) && t[2].equals(m)) return true;
        if (t[3].equals(m) && t[4].equals(m) && t[5].equals(m)) return true;
        if (t[6].equals(m) && t[7].equals(m) && t[8].equals(m)) return true;
        if (t[0].equals(m) && t[3].equals(m) && t[6].equals(m)) return true;
        if (t[1].equals(m) && t[4].equals(m) && t[7].equals(m)) return true;
        if (t[2].equals(m) && t[5].equals(m) && t[8].equals(m)) return true;
        if (t[0].equals(m) && t[4].equals(m) && t[8].equals(m)) return true;
        if (t[2].equals(m) && t[4].equals(m) && t[6].equals(m)) return true;
        return false;
    }

    private boolean comprobarEmpate(String[] t) {
        // CORRECCIÓN PROFESOR: Uso de Streams obligatorio en lugar de bucle for tradicional
        return Arrays.stream(t).noneMatch(c -> c.equals("-"));
    }

//    private boolean comprobarEmpate(String[] t) {
//        for (String c : t) {
//            if (c.equals("-")) {
//                return false; // Si encuentra un guion, significa que aún hay huecos, NO hay empate
//            }
//        }
//        return true; // Si el bucle termina y no encontró ningún guion, todo está lleno -> ¡Empate!
//    }
}