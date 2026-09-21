package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import dao.ObjetoGastoDAO;
import model.ObjetoGasto;
import model.Usuario;

@WebServlet("/ObjetoGastoServlet")
public class ObjetoGastoServlet extends HttpServlet
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
        ObjetoGastoDAO dao = new ObjetoGastoDAO();

        if (accion == null)
        {
            accion = "listar";
        }

        switch (accion)
        {
            case "buscar":
                String texto = request.getParameter("texto");
                List<ObjetoGasto> resultados = dao.buscar(texto);
                request.setAttribute("objetos", resultados);
                request.getRequestDispatcher("objetos.jsp").forward(request, response);
                break;

            case "listar":
                List<ObjetoGasto> lista = dao.listarTodos();
                request.setAttribute("objetos", lista);
                request.getRequestDispatcher("objetos.jsp").forward(request, response);
                break;

            case "listarActivos":
                List<ObjetoGasto> activos = dao.listarActivos();
                request.setAttribute("objetos", activos);
                request.getRequestDispatcher("objetos.jsp").forward(request, response);
                break;

            case "eliminar":
                if (!puedeModificar) {
                    response.sendRedirect("DashboardServlet?error=sinPermiso");
                    return;
                }
                int id = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(id);
                response.sendRedirect("ObjetoGastoServlet?accion=listar");
                break;

            case "obtener":
                int idObtener = Integer.parseInt(request.getParameter("id"));
                ObjetoGasto o = dao.obtenerPorId(idObtener);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                String json = "{"
                    + "\"id\":" + o.getIdObjetoGasto() + ","
                    + "\"codigo\":\"" + escapeJson(o.getCodigo()) + "\","
                    + "\"descripcion\":\"" + escapeJson(o.getDescripcion()) + "\","
                    + "\"estado\":" + o.getEstado()
                    + "}";

                response.getWriter().write(json);
                break;

            case "cambiarEstado":
                if (!puedeModificar) {
                    response.sendRedirect("DashboardServlet?error=sinPermiso");
                    return;
                }
                int idCambiar = Integer.parseInt(request.getParameter("id"));
                int estadoRecibido = Integer.parseInt(request.getParameter("estado"));
                ObjetoGasto objetoCambiar = dao.obtenerPorId(idCambiar);

                @SuppressWarnings("unchecked")
                Map<Integer, Integer> estadosAnteriores = (Map<Integer, Integer>) session.getAttribute("estadosAnterioresObjetos");
                if (estadosAnteriores == null) {
                    estadosAnteriores = new java.util.HashMap<>();
                    session.setAttribute("estadosAnterioresObjetos", estadosAnteriores);
                }

                if (estadoRecibido == 0)
                {
                    if (objetoCambiar != null && objetoCambiar.getEstado() != 6)
                    {
                        estadosAnteriores.put(idCambiar, objetoCambiar.getEstado());
                    }
                    dao.cambiarEstado(idCambiar, 6);
                }
                else
                {
                    Integer anterior = estadosAnteriores.remove(idCambiar);
                    dao.cambiarEstado(idCambiar, anterior == null ? 1 : anterior);
                }

                response.sendRedirect("ObjetoGastoServlet?accion=listar");
                break;

            default:
                response.sendRedirect("ObjetoGastoServlet?accion=listar");
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
        ObjetoGastoDAO dao = new ObjetoGastoDAO();

        if (accion == null)
        {
            response.sendRedirect("ObjetoGastoServlet?accion=listar");
            return;
        }

        switch (accion)
        {
            case "crear":
                String codigo = request.getParameter("codigo");
                String descripcion = request.getParameter("descripcion");

                ObjetoGasto nuevo = new ObjetoGasto();
                nuevo.setCodigo(codigo);
                nuevo.setDescripcion(descripcion);
                nuevo.setEstado(1);

                dao.crear(nuevo);
                response.sendRedirect("ObjetoGastoServlet?accion=listar");
                break;

            case "actualizar":
                int id = Integer.parseInt(request.getParameter("id"));
                String codigoUpd = request.getParameter("codigo");
                String descripcionUpd = request.getParameter("descripcion");

                ObjetoGasto actualizar = dao.obtenerPorId(id);
                if (actualizar != null)
                {
                    actualizar.setCodigo(codigoUpd);
                    actualizar.setDescripcion(descripcionUpd);
                    dao.actualizar(actualizar);
                }
                response.sendRedirect("ObjetoGastoServlet?accion=listar");
                break;

            default:
                response.sendRedirect("ObjetoGastoServlet?accion=listar");
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