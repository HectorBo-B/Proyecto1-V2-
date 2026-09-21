package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

import dao.ProveedorDAO;
import model.Proveedor;
import model.Usuario;

@WebServlet("/ProveedorServlet")
public class ProveedorServlet extends HttpServlet
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

        // ⭐ Validar rol
        String rol = user.getRol();
        boolean puedeVer = "ADMIN".equalsIgnoreCase(rol) || "CONTADOR".equalsIgnoreCase(rol) 
                         || "AUXILIAR".equalsIgnoreCase(rol) || "AUDITOR".equalsIgnoreCase(rol);
        if (!puedeVer) {
            response.sendRedirect("DashboardServlet?error=sinPermiso");
            return;
        }

        boolean puedeModificar = "ADMIN".equalsIgnoreCase(rol) || "CONTADOR".equalsIgnoreCase(rol) 
                               || "AUXILIAR".equalsIgnoreCase(rol);

        String accion = request.getParameter("accion");
        ProveedorDAO dao = new ProveedorDAO();

        if (accion == null)
        {
            accion = "listar";
        }

        switch (accion)
        {
            case "listar":
                List<Proveedor> lista = dao.listarTodos();
                request.setAttribute("proveedores", lista);
                request.getRequestDispatcher("proveedores.jsp").forward(request, response);
                break;

            case "buscar":
                String texto = request.getParameter("texto");
                List<Proveedor> resultados = dao.buscar(texto);
                request.setAttribute("proveedores", resultados);
                request.getRequestDispatcher("proveedores.jsp").forward(request, response);
                break;

            case "eliminar":
                if (!puedeModificar) {
                    response.sendRedirect("DashboardServlet?error=sinPermiso");
                    return;
                }
                int id = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(id);
                response.sendRedirect("ProveedorServlet?accion=listar");
                break;

            case "obtener":
                int idObtener = Integer.parseInt(request.getParameter("id"));
                Proveedor p = dao.obtenerPorId(idObtener);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String json = "{"
                    + "\"id\":" + p.getIdProveedor() + ","
                    + "\"nombre\":\"" + escapeJson(p.getNombre()) + "\","
                    + "\"ruc\":\"" + escapeJson(p.getRuc()) + "\","
                    + "\"telefono\":\"" + escapeJson(p.getTelefono() != null ? p.getTelefono() : "") + "\","
                    + "\"correo\":\"" + escapeJson(p.getCorreo() != null ? p.getCorreo() : "") + "\","
                    + "\"estado\":" + p.getEstado()
                    + "}";
                response.getWriter().write(json);
                break;

            case "cambiarEstado":
                if (!puedeModificar) {
                    response.sendRedirect("DashboardServlet?error=sinPermiso");
                    return;
                }
                int idCambiar = Integer.parseInt(request.getParameter("id"));
                int nuevoEstado = Integer.parseInt(request.getParameter("estado"));
                dao.cambiarEstado(idCambiar, nuevoEstado);
                response.sendRedirect("ProveedorServlet?accion=listar");
                break;

            default:
                response.sendRedirect("ProveedorServlet?accion=listar");
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

        // ⭐ Validar rol
        String rol = user.getRol();
        boolean puedeModificar = "ADMIN".equalsIgnoreCase(rol) || "CONTADOR".equalsIgnoreCase(rol) 
                               || "AUXILIAR".equalsIgnoreCase(rol);
        if (!puedeModificar) {
            response.sendRedirect("DashboardServlet?error=sinPermiso");
            return;
        }

        String accion = request.getParameter("accion");
        ProveedorDAO dao = new ProveedorDAO();

        if (accion == null)
        {
            response.sendRedirect("ProveedorServlet?accion=listar");
            return;
        }

        switch (accion)
        {
            case "crear":
                String nombre = request.getParameter("nombre");
                String telefono = request.getParameter("telefono");
                String correo = request.getParameter("correo");
                String ruc = request.getParameter("ruc");

                Proveedor nuevo = new Proveedor();
                nuevo.setNombre(nombre);
                nuevo.setTelefono(telefono);
                nuevo.setCorreo(correo);
                nuevo.setRuc(ruc);
                nuevo.setEstado(1);
                dao.crear(nuevo);
                response.sendRedirect("ProveedorServlet?accion=listar");
                break;

            case "actualizar":
                int id = Integer.parseInt(request.getParameter("id"));
                String nombreUpd = request.getParameter("nombre");
                String telefonoUpd = request.getParameter("telefono");
                String correoUpd = request.getParameter("correo");
                String rucUpd = request.getParameter("ruc");

                Proveedor actualizar = dao.obtenerPorId(id);
                if (actualizar != null)
                {
                    actualizar.setNombre(nombreUpd);
                    actualizar.setTelefono(telefonoUpd);
                    actualizar.setCorreo(correoUpd);
                    actualizar.setRuc(rucUpd);
                    dao.actualizar(actualizar);
                }
                response.sendRedirect("ProveedorServlet?accion=listar");
                break;

            default:
                response.sendRedirect("ProveedorServlet?accion=listar");
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