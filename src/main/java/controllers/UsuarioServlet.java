package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

import config.Encriptador;
import dao.UsuarioDAO;
import model.Usuario;

@WebServlet("/UsuarioServlet")
public class UsuarioServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        Usuario user = (Usuario) session.getAttribute("usuario");

        if (user == null)
        {
            response.sendRedirect("login.jsp");
            return;
        }

        // ⭐ Solo ADMIN puede gestionar usuarios
        if (!"ADMIN".equalsIgnoreCase(user.getRol()))
        {
            response.sendRedirect("DashboardServlet");
            return;
        }

        String accion = request.getParameter("accion");
        UsuarioDAO dao = new UsuarioDAO();

        if (accion == null) accion = "listar";

        switch (accion)
        {
            case "listar":
                List<Usuario> lista = dao.listarTodos();
                request.setAttribute("usuarios", lista);
                request.getRequestDispatcher("usuarios.jsp").forward(request, response);
                break;

            case "buscar":
                String texto = request.getParameter("texto");
                String rol = request.getParameter("rol");
                if (texto == null) texto = "";
                
                List<Usuario> resultados;
                if (rol != null && !rol.isEmpty() && !"TODOS".equals(rol)) {
                    resultados = dao.buscarConFiltros(texto, rol);
                } else {
                    resultados = dao.buscar(texto);
                }
                
                request.setAttribute("usuarios", resultados);
                request.getRequestDispatcher("usuarios.jsp").forward(request, response);
                break;

            case "obtener":
                int id = Integer.parseInt(request.getParameter("id"));
                Usuario u = dao.obtenerPorId(id);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                if (u != null) {
                    String json = "{"
                        + "\"idUsuario\":" + u.getIdUsuario() + ","
                        + "\"nombre\":\"" + escapeJson(u.getNombre()) + "\","
                        + "\"apellido\":\"" + escapeJson(u.getApellido()) + "\","
                        + "\"usuario\":\"" + escapeJson(u.getUsuario()) + "\","
                        + "\"correo\":\"" + escapeJson(u.getCorreo()) + "\","
                        + "\"rol\":\"" + escapeJson(u.getRol()) + "\","
                        + "\"estado\":" + u.getEstado()
                        + "}";
                    response.getWriter().write(json);
                } else {
                    response.getWriter().write("{}");
                }
                break;

            case "cambiarEstado":
                int idCam = Integer.parseInt(request.getParameter("id"));
                int nuevoEstado = Integer.parseInt(request.getParameter("estado"));
                dao.cambiarEstado(idCam, nuevoEstado);
                response.sendRedirect("UsuarioServlet?accion=listar");
                break;

            default:
                response.sendRedirect("UsuarioServlet?accion=listar");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        Usuario user = (Usuario) session.getAttribute("usuario");

        if (user == null)
        {
            response.sendRedirect("login.jsp");
            return;
        }

        if (!"ADMIN".equalsIgnoreCase(user.getRol()))
        {
            response.sendRedirect("DashboardServlet");
            return;
        }

        String accion = request.getParameter("accion");
        UsuarioDAO dao = new UsuarioDAO();

        if (accion == null)
        {
            response.sendRedirect("UsuarioServlet?accion=listar");
            return;
        }

        switch (accion)
        {
            case "crear":
                String nombre = request.getParameter("nombre");
                String apellido = request.getParameter("apellido");
                String username = request.getParameter("usuario");
                String password = request.getParameter("password");
                String correo = request.getParameter("correo");
                String rol = request.getParameter("rol");

                // ⭐ Validar unicidad
                if (dao.existeUsuario(username)) {
                    response.sendRedirect("UsuarioServlet?accion=listar&error=usuarioExiste");
                    return;
                }
                if (dao.existeCorreo(correo)) {
                    response.sendRedirect("UsuarioServlet?accion=listar&error=correoExiste");
                    return;
                }

                Usuario nuevo = new Usuario();
                nuevo.setNombre(nombre);
                nuevo.setApellido(apellido);
                nuevo.setUsuario(username);
                nuevo.setPassword(Encriptador.encriptarSHA256(password));
                nuevo.setCorreo(correo);
                nuevo.setRol(rol);
                nuevo.setEstado(1);

                dao.crear(nuevo);
                response.sendRedirect("UsuarioServlet?accion=listar");
                break;

                        case "actualizar":
                int idUpd = Integer.parseInt(request.getParameter("id"));
                String nombreUpd = request.getParameter("nombre").trim();
                String apellidoUpd = request.getParameter("apellido").trim();
                String usernameUpd = request.getParameter("usuario").trim();
                String passwordUpd = request.getParameter("password");
                String correoUpd = request.getParameter("correo").trim();
                String rolUpd = request.getParameter("rol");

                Usuario existente = dao.obtenerPorId(idUpd);
                if (existente == null) {
                    response.sendRedirect("UsuarioServlet?accion=listar");
                    return;
                }

                // ⭐ Validar usuario único (ignorando mayúsculas y espacios)
                String usuarioOriginal = existente.getUsuario().trim();
                if (!usuarioOriginal.equalsIgnoreCase(usernameUpd) && dao.existeUsuario(usernameUpd)) {
                    response.sendRedirect("UsuarioServlet?accion=listar&error=usuarioExiste");
                    return;
                }

                // ⭐ Validar correo único (ignorando mayúsculas y espacios)
                String correoOriginal = existente.getCorreo().trim();
                if (!correoOriginal.equalsIgnoreCase(correoUpd) && dao.existeCorreo(correoUpd)) {
                    response.sendRedirect("UsuarioServlet?accion=listar&error=correoExiste");
                    return;
                }

                existente.setNombre(nombreUpd);
                existente.setApellido(apellidoUpd);
                existente.setUsuario(usernameUpd);
                existente.setCorreo(correoUpd);
                existente.setRol(rolUpd);

                boolean cambiarPassword = (passwordUpd != null && !passwordUpd.isEmpty());
                if (cambiarPassword) {
                    existente.setPassword(Encriptador.encriptarSHA256(passwordUpd));
                }

                dao.actualizar(existente, cambiarPassword);
                response.sendRedirect("UsuarioServlet?accion=listar");
                break;

            default:
                response.sendRedirect("UsuarioServlet?accion=listar");
        }
    }

    private String escapeJson(String texto)
    {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}