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

@WebServlet("/contador")
public class ContadorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        // 1. Abrir o recuperar la sesión
        HttpSession session = req.getSession();

        // 2. Leer si ya existe el atributo "numVisitas" en la sesión
        Integer visitas = (Integer) session.getAttribute("numVisitas");

        // 3. Si es la primera vez, empieza en 1; si ya existía, sumamos 1
        if (visitas == null) {
            visitas = 1;
        } else {
            visitas++;
        }

        // 4. Guardar el valor actualizado de vuelta en la sesión
        session.setAttribute("numVisitas", visitas);

        // 5. Preparar Thymeleaf
        var webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("visitas", visitas);

        // 6. Procesar la plantilla HTML
        TemplateEngine engine = (TemplateEngine) getServletContext().getAttribute("com.daw.TemplateEngine");
        engine.process("contador", ctx, resp.getWriter());
    }
}