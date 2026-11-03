package org.example;

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

@WebServlet("/ahorcado")
public class AhorcadoServlet extends HttpServlet {

    private static final String PALABRA_SECRETA = "JAVA"; // Palabra a adivinar

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        // 1. Inicializar el estado del juego en la sesión si no existe
        char[] estadoGuiones = (char[]) session.getAttribute("estadoGuiones");
        Integer intentos = (Integer) session.getAttribute("intentos");
        Boolean ganado = (Boolean) session.getAttribute("ganado");
        Boolean perdido = (Boolean) session.getAttribute("perdido");

        // Si es una nueva partida o viene el parámetro ?reiniciar=true
        if (estadoGuiones == null || "true".equals(req.getParameter("reiniciar"))) {
            estadoGuiones = new char[]{'_', '_', '_', '_'}; // Tantos guiones como letras tenga "JAVA"
            intentos = 6; // Intentos máximos
            ganado = false;
            perdido = false;
            session.setAttribute("estadoGuiones", estadoGuiones);
            session.setAttribute("intentos", intentos);
            session.setAttribute("ganado", ganado);
            session.setAttribute("perdido", perdido);
        }

        String mensajeError = null;
        String letraParam = req.getParameter("letra");

        // 2. Si el juego sigue activo y el usuario ha enviado una letra por la URL (?letra=a)
        if (letraParam != null && !ganado && !perdido) {
            char letra = Character.toUpperCase(letraParam.charAt(0));
            boolean acierto = false;

            // Comprobar si la letra está en la palabra secreta
            for (int i = 0; i < PALABRA_SECRETA.length(); i++) {
                if (PALABRA_SECRETA.charAt(i) == letra) {
                    estadoGuiones[i] = letra; // Desvelar la letra
                    acierto = true;
                }
            }

            if (!acierto) {
                intentos--; // Si falla, restamos un intento
            }

            // Comprobar si ha ganado (ya no quedan guiones '_')
            String palabraActual = new String(estadoGuiones);
            if (palabraActual.equals(PALABRA_SECRETA)) {
                ganado = true;
            } else if (intentos <= 0) {
                perdido = true;
            }

            // Actualizar la sesión
            session.setAttribute("estadoGuiones", estadoGuiones);
            session.setAttribute("intentos", intentos);
            session.setAttribute("ganado", ganado);
            session.setAttribute("perdido", perdido);
        }

        // 3. Preparar el contexto para Thymeleaf
        var webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);

        // Convertimos el array de char en un String con espacios para que se vea bien (ej: J _ V A)
        ctx.setVariable("palabraVisible", String.valueOf(estadoGuiones).replace("", " ").trim());
        ctx.setVariable("intentos", intentos);
        ctx.setVariable("ganado", ganado);
        ctx.setVariable("perdido", perdido);

        resp.setContentType("text/html;charset=UTF-8");
        // Nota: Asegúrate de usar tu TemplateEngine configurado (aquí asumo el atributo estándar)
        TemplateEngine engine = (TemplateEngine) getServletContext().getAttribute("com.daw.TemplateEngine");
        engine.process("ahorcado", ctx, resp.getWriter());
    }
}