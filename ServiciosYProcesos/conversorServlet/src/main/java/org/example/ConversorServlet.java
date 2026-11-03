package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@WebServlet("/conversor")
public class ConversorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        // 1. Recoger el parámetro de la URL (?euros=50)
        String eurosStr = req.getParameter("euros");

        Double euros = 0.0;
        Double dolares = 0.0;
        boolean convertido = false;

        if (eurosStr != null && !eurosStr.isEmpty()) {
            try {
                euros = Double.parseDouble(eurosStr);
                dolares = euros * 1.08; // Tipo de cambio de ejemplo
                convertido = true;
            } catch (NumberFormatException e) {
                // Por si escriben letras en vez de números
                euros = 0.0;
            }
        }

        // 2. Preparar el contexto de Thymeleaf
        var webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);

        // 3. Pasar las variables al HTML
        ctx.setVariable("euros", euros);
        ctx.setVariable("dolares", dolares);
        ctx.setVariable("convertido", convertido);

        // 4. Procesar la plantilla HTML
        TemplateEngine engine = (TemplateEngine) getServletContext().getAttribute("com.daw.TemplateEngine");
        engine.process("conversor", ctx, resp.getWriter());
    }
}