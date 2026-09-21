package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import dao.ChequeDAO;
import dao.HistorialChequeDAO;
import dao.ProveedorDAO;
import dao.ObjetoGastoDAO;
import model.Cheque;
import model.Proveedor;
import model.ObjetoGasto;
import model.Usuario;

@WebServlet("/ChequeServlet")
public class ChequeServlet extends HttpServlet
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
        ChequeDAO dao = new ChequeDAO();
        HistorialChequeDAO historialDAO = new HistorialChequeDAO();

        if (accion == null)
        {
            accion = "listar";
        }

        switch (accion)
        {
            case "listar":
                List<Cheque> lista = dao.listarTodos();
                request.setAttribute("cheques", lista);
                cargarDatosParaFormulario(request);
                request.getRequestDispatcher("cheques.jsp").forward(request, response);
                break;

            case "buscar":
                String texto = request.getParameter("texto");
                int estadoFiltro = 0;
                String estadoParam = request.getParameter("estado");
                if (estadoParam != null && !estadoParam.isEmpty()) {
                    try { estadoFiltro = Integer.parseInt(estadoParam); } catch (NumberFormatException ignored) {}
                }
                List<Cheque> resultados = dao.buscar(texto, estadoFiltro);
                request.setAttribute("cheques", resultados);
                cargarDatosParaFormulario(request);
                request.getRequestDispatcher("cheques.jsp").forward(request, response);
                break;

            case "listarPorEstado":
                int estado = Integer.parseInt(request.getParameter("estado"));
                List<Cheque> filtrados = dao.listarPorEstado(estado);
                request.setAttribute("cheques", filtrados);
                cargarDatosParaFormulario(request);
                request.getRequestDispatcher("cheques.jsp").forward(request, response);
                break;

            case "obtener":
                int id = Integer.parseInt(request.getParameter("id"));
                Cheque c = dao.obtenerPorId(id);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                if (c != null) {
                    String json = "{"
                        + "\"idCheque\":" + c.getIdCheque() + ","
                        + "\"numeroCheque\":\"" + escapeJson(c.getNumeroCheque()) + "\","
                        + "\"fechaCheque\":\"" + c.getFechaCheque() + "\","
                        + "\"idProveedor\":" + c.getIdProveedor() + ","
                        + "\"idObjetoGasto\":" + c.getIdObjetoGasto() + ","
                        + "\"monto\":\"" + c.getMonto() + "\","
                        + "\"montoLetras\":\"" + escapeJson(c.getMontoLetras() != null ? c.getMontoLetras() : "") + "\","
                        + "\"detalle\":\"" + escapeJson(c.getDetalle() != null ? c.getDetalle() : "") + "\","
                        + "\"estado\":" + c.getEstado()
                        + "}";
                    response.getWriter().write(json);
                } else {
                    response.getWriter().write("{}");
                }
                break;

            case "nuevo":
                cargarDatosParaFormulario(request);
                request.setAttribute("modo", "crear");
                request.getRequestDispatcher("cheques.jsp").forward(request, response);
                break;

            case "editar":
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Cheque editar = dao.obtenerPorId(idEditar);
                request.setAttribute("cheque", editar);
                request.setAttribute("modo", "editar");
                cargarDatosParaFormulario(request);
                request.getRequestDispatcher("cheques.jsp").forward(request, response);
                break;

            case "anular":
                if (!puedeModificar) {
                    response.sendRedirect("DashboardServlet?error=sinPermiso");
                    return;
                }
                int idAnular = Integer.parseInt(request.getParameter("id"));
                String motivo = request.getParameter("motivo");
                Cheque chequeAnular = dao.obtenerPorId(idAnular);
                if (chequeAnular != null && chequeAnular.getEstado() == 1 && motivo != null && !motivo.isEmpty())
                {
                    int estadoAnterior = chequeAnular.getEstado();
                    dao.anularCheque(idAnular, motivo, user.getIdUsuario());
                    // ⭐ Registrar en historial
                    historialDAO.registrar(idAnular, estadoAnterior, 4, user.getIdUsuario(), motivo);
                }
                response.sendRedirect("ChequeServlet?accion=listar");
                break;

            case "sacarCirculacion":
                if (!puedeModificar) {
                    response.sendRedirect("DashboardServlet?error=sinPermiso");
                    return;
                }
                int idSacar = Integer.parseInt(request.getParameter("id"));
                String observacion = request.getParameter("observacion");
                Cheque chequeSacar = dao.obtenerPorId(idSacar);
                if (chequeSacar != null && chequeSacar.getEstado() == 1 && observacion != null && !observacion.isEmpty())
                {
                    int estadoAnterior = chequeSacar.getEstado();
                    dao.sacarDeCirculacion(idSacar, observacion);
                    // ⭐ Registrar en historial
                    historialDAO.registrar(idSacar, estadoAnterior, 5, user.getIdUsuario(), observacion);
                }
                response.sendRedirect("ChequeServlet?accion=listar");
                break;

            case "obtenerNumero":
                String siguienteNumero = dao.obtenerSiguienteNumeroCheque();
                response.setContentType("application/json");
                response.getWriter().write("{\"numero\":\"" + siguienteNumero + "\"}");
                break;

            default:
                response.sendRedirect("ChequeServlet?accion=listar");
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
        ChequeDAO dao = new ChequeDAO();
        HistorialChequeDAO historialDAO = new HistorialChequeDAO();

        if (accion == null)
        {
            response.sendRedirect("ChequeServlet?accion=listar");
            return;
        }

        switch (accion)
        {
            case "crear":
                String numero = request.getParameter("numero_cheque");
                Date fecha = Date.valueOf(request.getParameter("fecha_cheque"));
                int idProveedor = Integer.parseInt(request.getParameter("id_proveedor"));
                String montoStr = request.getParameter("monto").replace(",", "");
                BigDecimal monto = new BigDecimal(montoStr);
                String montoLetras = request.getParameter("monto_letras");
                String detalle = request.getParameter("detalle");
                int idObjeto = Integer.parseInt(request.getParameter("id_objeto_gasto"));

                Cheque nuevo = new Cheque();
                nuevo.setNumeroCheque(numero);
                nuevo.setFechaCheque(fecha);
                nuevo.setIdProveedor(idProveedor);
                nuevo.setMonto(monto);
                nuevo.setMontoLetras(montoLetras);
                nuevo.setDetalle(detalle);
                nuevo.setIdObjetoGasto(idObjeto);
                nuevo.setEstado(1);
                nuevo.setIdUsuarioCreacion(user.getIdUsuario());

                boolean creado = dao.crear(nuevo);

                // ⭐ Registrar creación en historial
                if (creado) {
                    historialDAO.registrar(nuevo.getIdCheque(), 0, 1, user.getIdUsuario(), "Creación del cheque");
                }

                response.sendRedirect("ChequeServlet?accion=listar");
                break;

            case "actualizar":
                int id = Integer.parseInt(request.getParameter("id"));
                String numeroUpd = request.getParameter("numero_cheque");
                Date fechaUpd = Date.valueOf(request.getParameter("fecha_cheque"));
                int idProveedorUpd = Integer.parseInt(request.getParameter("id_proveedor"));
                String montoUpdStr = request.getParameter("monto").replace(",", "");
                BigDecimal montoUpd = new BigDecimal(montoUpdStr);
                String montoLetrasUpd = request.getParameter("monto_letras");
                String detalleUpd = request.getParameter("detalle");
                int idObjetoUpd = Integer.parseInt(request.getParameter("id_objeto_gasto"));

                Cheque actualizar = dao.obtenerPorId(id);

                if (actualizar != null && actualizar.getEstado() == 1)
                {
                    actualizar.setNumeroCheque(numeroUpd);
                    actualizar.setFechaCheque(fechaUpd);
                    actualizar.setIdProveedor(idProveedorUpd);
                    actualizar.setMonto(montoUpd);
                    actualizar.setMontoLetras(montoLetrasUpd);
                    actualizar.setDetalle(detalleUpd);
                    actualizar.setIdObjetoGasto(idObjetoUpd);
                    dao.actualizar(actualizar);
                }
                response.sendRedirect("ChequeServlet?accion=listar");
                break;

            default:
                response.sendRedirect("ChequeServlet?accion=listar");
        }
    }

    private void cargarDatosParaFormulario(HttpServletRequest request)
    {
        ProveedorDAO proveedorDAO = new ProveedorDAO();
        ObjetoGastoDAO objetoDAO = new ObjetoGastoDAO();
        List<Proveedor> proveedores = proveedorDAO.listarActivos();
        List<ObjetoGasto> objetos = objetoDAO.listarActivos();
        request.setAttribute("proveedores", proveedores);
        request.setAttribute("objetos", objetos);
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