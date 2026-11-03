package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/procesar")
public class ProcesarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Configurar la codificación para que no salgan símbolos raros con tildes
        response.setContentType("text/html;charset=UTF-8");

        // 2. Recoger los parámetros del formulario HTML
        String nombre = request.getParameter("nombre");
        String edadStr = request.getParameter("edad");

        int edad = Integer.parseInt(edadStr);

        // 3. Lógica del negocio (ejemplo típico de examen)
        String mensajeMayorEdad = (edad >= 18) ? "Eres MAYOR de edad." : "Eres MENOR de edad.";

        // 4. Escribir la respuesta HTML directamente en pantalla
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>Resultado</title></head>");
        out.println("<body>");
        out.println("<h1>Resultado del Servidor</h1>");
        out.println("<p>Hola, <strong>" + nombre + "</strong>.</p>");
        out.println("<p>Tienes " + edad + " años. " + mensajeMayorEdad + "</p>");
        out.println("<br><a href='index.html'>Volver al formulario</a>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Por si alguien entra por GET, redirigimos o ejecutamos lo mismo
        doPost(request, response);
    }
}