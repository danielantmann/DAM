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

@WebServlet("/anadir")
public class addTarea extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String titulo = req.getParameter("titulo");
        String descripcion = req.getParameter("descripcion");
        String prioridad = req.getParameter("prioridad");

        HttpSession session = req.getSession(false);
        ListaTareas listaTareas = (session != null) ? (ListaTareas) session.getAttribute("listaTarea") : null;

        if (listaTareas == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No hay ninguna lista activa. Configura una primero en /crearLista.");
            return;
        }

        if (titulo == null ||  descripcion == null || prioridad == null ){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros obligatorios.");
            return;
        }


        int num;

        try {
            num = Integer.parseInt(prioridad);

            if (num <= 0 || num >=4) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El número debe ser entre 1 y 3.");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El formato de las prioridad no es válido.");
            return;
        }


        Tarea tarea = new Tarea(0, titulo,descripcion,num);


        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");

        if (listaTareas.puedesAñadir()){
            listaTareas.añadirTarea(tarea);
            out.println("<h2>Tarea agregada.</h2>");
        }else{
            out.println("<h2>Error, no se pueden agregar tareas.</h2>");
        }


        out.close();

    }
}
