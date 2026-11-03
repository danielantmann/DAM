package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        String user = req.getParameter("usuario");
        String pass = req.getParameter("password");

        // Comprobación de credenciales
        if ("admin".equals(user) && "1234".equals(pass)) {

            // Abrimos sesión y guardamos al usuario
            HttpSession session = req.getSession();
            session.setAttribute("usuarioLogueado", user);

            // Redirigimos al perfil protegido
            resp.sendRedirect("perfil");

        } else {
            // Si falla, redirigimos de vuelta a la página del login con Thymeleaf
            resp.getWriter().println("<h3>Error: Usuario o contraseña incorrectos.</h3>");
            resp.getWriter().println("<p><a href='login-page'>Volver a intentar</a></p>");
        }
    }
}