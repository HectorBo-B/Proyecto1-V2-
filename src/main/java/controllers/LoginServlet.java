package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import dao.EntradaSalidaDAO;
import dao.UsuarioDAO;
import model.Usuario;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private static final int MAX_INTENTOS = 3;
    private static final int SEGUNDOS_BLOQUEO = 5;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");

        UsuarioDAO dao = new UsuarioDAO();
        EntradaSalidaDAO esDAO = new EntradaSalidaDAO();

        // ⭐ 1. Verificar si el usuario existe y está bloqueado
        Usuario existente = dao.obtenerPorUsuario(usuario);

        if (existente != null)
        {
            int segundosRestantes = dao.segundosBloqueoRestantes(existente.getIdUsuario());

            if (segundosRestantes > 0)
            {
                // Está bloqueado → solo el contador
                request.setAttribute("segundosBloqueo", segundosRestantes);
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }
            // ⭐ Si el bloqueo expiró, NO se resetean los intentos.
            // Los intentos siguen acumulándose hasta que el usuario acierte.
        }

        // ⭐ 2. Intentar login
        Usuario user = dao.validarLogin(usuario, password);

        if (user != null)
        {
            // ⭐ Login exitoso
            int intentosPrevios = user.getIntentosFallidos();
            esDAO.registrarEntrada(user.getIdUsuario(), intentosPrevios);

            dao.resetearIntentos(user.getIdUsuario());
            user.setIntentosFallidos(0);

            HttpSession session = request.getSession();
            session.setAttribute("usuario", user);
            response.sendRedirect("DashboardServlet");
        }
        else
        {
            // ⭐ Login fallido
            if (existente != null)
            {
                int intentos = existente.getIntentosFallidos() + 1;
                dao.actualizarIntentosFallidos(existente.getIdUsuario(), intentos);
                esDAO.actualizarIntentosFallidos(existente.getIdUsuario(), intentos);

                if (intentos >= MAX_INTENTOS) {
                    dao.bloquearUsuarioPorSegundos(existente.getIdUsuario(), SEGUNDOS_BLOQUEO);
                    request.setAttribute("segundosBloqueo", SEGUNDOS_BLOQUEO);
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                    return;
                }
            }

            request.setAttribute("error", "Usuario o contraseña incorrectos");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String accion = request.getParameter("accion");

        if ("logout".equals(accion))
        {
            HttpSession session = request.getSession(false);
            if (session != null)
            {
                Usuario user = (Usuario) session.getAttribute("usuario");
                if (user != null) {
                    EntradaSalidaDAO esDAO = new EntradaSalidaDAO();
                    esDAO.registrarSalida(user.getIdUsuario());
                }
                session.invalidate();
            }
            response.sendRedirect("login.jsp");
            return;
        }

        response.sendRedirect("login.jsp");
    }
}