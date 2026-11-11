package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.modelo.ActivacionMisiles;

import java.io.IOException;

@WebServlet("/combinacion")
public class Combinacion extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String num = req.getParameter("num");
        HttpSession session = req.getSession();
        if (num == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"falta el numero");
            return;
        }

        ActivacionMisiles am = (ActivacionMisiles) req.getSession().getAttribute("activacion");

        if (am == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"falta la configuracion");
            return;
        }



        int posicionToca =  am.getFase() -1;
        char digitoCorrecto = am.getNumSecreto().charAt(posicionToca);
        char digitoEnviado = num.charAt(0);

        if (digitoEnviado == digitoCorrecto) {
            // ¡Acierta! Avanzamos de fase
            am.setFase(am.getFase() + 1);

            // ¿Hemos superado la longitud total del número secreto?
            // Si el secreto era "1234" (largo 4), y la fase pasa a ser 5, ¡significa que ha ganado!
            if (am.getFase() > am.getNumSecreto().length()) {
                resp.getWriter().println("¡VICTORIA! Misil desactivado por completo.");
                session.invalidate();
            } else {
                resp.getWriter().println("¡Correcto! Ve a la siguiente fase.");
            }
        } else {
            resp.getWriter().println("¡ALARMA! Código incorrecto. Vuelve a empezar.");

            session.invalidate();
        }

    }
}
