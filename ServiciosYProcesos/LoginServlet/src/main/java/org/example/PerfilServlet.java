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

@WebServlet("/perfil")
public class PerfilServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        // 1. Recuperar sesión sin crear una nueva
        HttpSession session = req.getSession(false);

        String usuario = null;
        if (session != null) {
            usuario = (String) session.getAttribute("usuarioLogueado");
        }

        // 2. Filtro de seguridad: si no hay sesión, fuera al login
        if (usuario == null) {
            resp.sendRedirect("login-page");
            return;
        }

        // 3. Cargar Thymeleaf para la vista de perfil
        var webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("usuario", usuario);

        TemplateEngine engine = (TemplateEngine) getServletContext().getAttribute("com.daw.TemplateEngine");
        engine.process("perfil", ctx, resp.getWriter());
    }
}