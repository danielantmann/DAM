package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.model.ListaTareas;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/crear")
public class CrearLista extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String numMax = req.getParameter("num");

        if (numMax == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros obligatorios.");
            return;
        }

        int num;

        try {
            num = Integer.parseInt(numMax);

            if (num <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El número máximo debe ser mayor que 0.");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El formato de las num no es válido.");
            return;
        }

        ListaTareas listaTareas = new ListaTareas(num);
        HttpSession session = req.getSession();
        session.setAttribute("listaTarea",listaTareas);

        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Lista tareas configurada con éxito</h1>");

        out.close();

    }
}
