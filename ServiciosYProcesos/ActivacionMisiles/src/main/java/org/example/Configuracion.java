package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.modelo.ActivacionMisiles;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/config")
public class Configuracion  extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       String nombre = req.getParameter("nombre");
       String numSecreto = req.getParameter("numSecreto");


        if (nombre == null || numSecreto ==null){
            resp.sendError(400);
            return;
        }

        ActivacionMisiles am = new ActivacionMisiles(nombre,numSecreto);

        req.getSession().setAttribute("activacion" , am);

        resp.setContentType("text/html;charset=utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<body>");
        out.println("<h1> "+nombre+" configuracion hecha </h1>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }
}
