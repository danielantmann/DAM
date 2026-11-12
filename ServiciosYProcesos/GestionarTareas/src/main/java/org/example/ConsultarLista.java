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

@WebServlet("/lista")
public class ConsultarLista extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        ListaTareas listaTareas = (session != null) ? (ListaTareas) session.getAttribute("listaTarea") : null;

        if (listaTareas == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No hay ninguna lista activa. Configura una primero en /crearLista.");
            return;
        }

        int cantCompletas = 0;
        int pendientes = 0;
        for (Tarea t : listaTareas.getListaTareas()){
            if (t.isCompletada()){
                cantCompletas ++;
            }else {
                pendientes ++;
            }

        }

        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        out.println("<h1>Lista de tareas!</h1>");
        out.println("<h2>Total tareas :  " + listaTareas.cantTareas() +"</h2>");
        out.println("<h2>Tareas completadas: "+ cantCompletas+ "</h2>");
        out.println("<h2>Tareas pendientes: "+ pendientes + "</h2>");
        out.println("</body></html>");
        out.close();
    }
}