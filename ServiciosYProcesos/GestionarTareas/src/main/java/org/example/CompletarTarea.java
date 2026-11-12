package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.model.ListaTareas;
import org.example.model.Tarea;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/completar") // 👈 1. Añadida la barra '/'
public class CompletarTarea extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        if (id == null || id.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros obligatorios.");
            return;
        }

        HttpSession session = req.getSession(false);
        ListaTareas listaTareas = (session != null) ? (ListaTareas) session.getAttribute("listaTarea") : null;

        if (listaTareas == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No hay ninguna lista activa. Configura una primero en /crearLista.");
            return;
        }

        int numId;

        try {
            numId = Integer.parseInt(id);

            if (numId <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El ID proporcionado no es válido.");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El formato del ID no es válido.");
            return;
        }

        // Buscamos la tarea
        Tarea t = listaTareas.buscarTarea(numId);

        // 👈 2. Comprobamos si la tarea realmente existe para evitar NullPointerException
        if (t == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No existe ninguna tarea con ese ID.");
            return;
        }

        // Marcamos como completada
        t.setCompletada(true);

        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        out.println("<h1>¡Tarea completada con éxito!</h1>");
        out.println("</body></html>");
        out.close();
    }
}