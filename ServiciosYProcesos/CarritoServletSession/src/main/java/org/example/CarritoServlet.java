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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/carrito")
public class CarritoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        // 1. Abrir o recuperar la sesión
        HttpSession session = req.getSession();

        // 2. Recuperar la lista de la sesión (o crearla si no existe todavía)
        List<String> carrito = (List<String>) session.getAttribute("listaCompra");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("listaCompra", carrito);
        }

        // 3. Leer el parámetro de la URL (ej: ?producto=Manzana) y añadirlo si viene relleno
        String nuevoProducto = req.getParameter("producto");
        if (nuevoProducto != null && !nuevoProducto.trim().isEmpty()) {
            carrito.add(nuevoProducto);
        }

        // 4. Preparar el motor de Thymeleaf
        var webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("carrito", carrito);

        // 5. Procesar la plantilla HTML
        TemplateEngine engine = (TemplateEngine) getServletContext().getAttribute("com.daw.TemplateEngine");
        engine.process("carrito", ctx, resp.getWriter());
    }
}