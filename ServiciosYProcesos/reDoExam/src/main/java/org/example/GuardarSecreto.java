package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.modelo.CajaFuerte;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/guardar")
public class GuardarSecreto extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nombre = req.getParameter("nombreSecreto");
        String valor = req.getParameter("valorSecreto");


        //url guardar?nombreSecreto=pepe&valorSecreto=gol
        CajaFuerte cajaFuerte = (CajaFuerte) req.getSession().getAttribute("cajaFuerte");


        if ( cajaFuerte == null ){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"falta la configuracion");
            return;
        }

        if (! cajaFuerte.isAbierta()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"primero abrela");
            return;
        }

        cajaFuerte.getSecretos().put(nombre,valor);
        //req.getSession().setAttribute(nombre,valor);

        resp.setContentType("text/html;charset=utf-8");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<body>");
        out.println("<h1> secreto guardado </h1>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }
}
