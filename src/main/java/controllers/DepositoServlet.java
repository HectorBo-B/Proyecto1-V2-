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

import dao.DepositoDAO;
import model.Deposito;
import model.Usuario;

@WebServlet("/DepositoServlet")
public class DepositoServlet extends HttpServlet
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
        DepositoDAO dao = new DepositoDAO();
        
        if (accion == null)
        {
            accion = "listar";
        }
        
        switch (accion)
        {
            case "buscar":
                String texto = request.getParameter("texto");
                List<Deposito> resultados = dao.buscar(texto);
                request.setAttribute("depositos", resultados);
                request.getRequestDispatcher("depositos.jsp").forward(request, response);
                break;
            
            case "listar":
                List<Deposito> lista = dao.listarTodos();
                request.setAttribute("depositos", lista);
                request.getRequestDispatcher("depositos.jsp").forward(request, response);
                break;
                
            case "listarActivos":
                List<Deposito> activos = dao.listarActivos();
                request.setAttribute("depositos", activos);
                request.getRequestDispatcher("depositos.jsp").forward(request, response);
                break;
                
            case "listarPorTipo":
                int tipo = Integer.parseInt(request.getParameter("tipo"));
                List<Deposito> filtrados = dao.listarPorTipo(tipo);
                request.setAttribute("depositos", filtrados);
                request.getRequestDispatcher("depositos.jsp").forward(request, response);
                break;
                
            case "obtener":
                int idObtener = Integer.parseInt(request.getParameter("id"));
                Deposito d = dao.obtenerPorId(idObtener);
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                String json = "{"
                    + "\"id\":" + d.getIdDeposito() + ","
                    + "\"numero_comprobante\":\"" + escapeJson(d.getNumeroComprobante()) + "\","
                    + "\"tipo_deposito\":" + d.getTipoDeposito() + ","
                    + "\"fecha\":\"" + d.getFecha() + "\","
                    + "\"monto\":\"" + d.getMonto() + "\","
                    + "\"detalle\":\"" + escapeJson(d.getDetalle() != null ? d.getDetalle() : "") + "\","
                    + "\"estado\":" + d.getEstado()
                    + "}";
                
                response.getWriter().write(json);
                break;
                
            case "eliminar":
                if (!puedeModificar) {
                    response.sendRedirect("DashboardServlet?error=sinPermiso");
                    return;
                }
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("DepositoServlet?accion=listar");
                break;
                
            case "cambiarEstado":
                if (!puedeModificar) {
                    response.sendRedirect("DashboardServlet?error=sinPermiso");
                    return;
                }
                int idCambiar = Integer.parseInt(request.getParameter("id"));
                int nuevoEstado = Integer.parseInt(request.getParameter("estado"));
                dao.cambiarEstado(idCambiar, nuevoEstado);
                response.sendRedirect("DepositoServlet?accion=listar");
                break;
                
            default:
                response.sendRedirect("DepositoServlet?accion=listar");
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
        DepositoDAO dao = new DepositoDAO();
        
        if (accion == null)
        {
            response.sendRedirect("DepositoServlet?accion=listar");
            return;
        }
        
        switch (accion)
        {
            case "crear":
                String numeroComprobante = request.getParameter("numero_comprobante");
                int tipoDeposito = Integer.parseInt(request.getParameter("tipo_deposito"));
                Date fecha = Date.valueOf(request.getParameter("fecha"));
                String montoStr = request.getParameter("monto").replace(",", "");
                BigDecimal monto = new BigDecimal(montoStr);
                String detalle = request.getParameter("detalle");
                
                Deposito nuevo = new Deposito();
                nuevo.setNumeroComprobante(numeroComprobante);
                nuevo.setTipoDeposito(tipoDeposito);
                nuevo.setFecha(fecha);
                nuevo.setMonto(monto);
                nuevo.setDetalle(detalle);
                nuevo.setEstado(1);
                nuevo.setIdUsuario(user.getIdUsuario());
                
                dao.crear(nuevo);
                response.sendRedirect("DepositoServlet?accion=listar");
                break;
                
            case "actualizar":
                int id = 0;
                try { id = Integer.parseInt(request.getParameter("id")); } catch (Exception e) { id = 0; }

                if (id <= 0) {
                    response.sendRedirect("DepositoServlet?accion=listar");
                    return;
                }

                String numeroComprobanteUpd = request.getParameter("numero_comprobante");
                String fechaStr = request.getParameter("fecha");
                String montoUpdStr = request.getParameter("monto");
                String detalleUpd = request.getParameter("detalle");
                String estadoStr = request.getParameter("estado");
                String tipoStr = request.getParameter("tipo_deposito");

                Deposito actualizar = dao.obtenerPorId(id);

                if (actualizar != null)
                {
                    actualizar.setNumeroComprobante(numeroComprobanteUpd != null ? numeroComprobanteUpd : "");

                    if (fechaStr != null && !fechaStr.isEmpty() && !fechaStr.equals("undefined")) {
                        try { actualizar.setFecha(Date.valueOf(fechaStr)); } catch (Exception ignored) {}
                    }

                    if (montoUpdStr != null && !montoUpdStr.isEmpty() && !montoUpdStr.equals("undefined")) {
                        try {
                            BigDecimal montoUpd = new BigDecimal(montoUpdStr.replace(",", ""));
                            actualizar.setMonto(montoUpd);
                        } catch (Exception ignored) {}
                    }

                    actualizar.setDetalle(detalleUpd != null ? detalleUpd : "");

                    if (tipoStr != null && !tipoStr.isEmpty() && !tipoStr.equals("undefined")) {
                        try { actualizar.setTipoDeposito(Integer.parseInt(tipoStr)); } catch (Exception ignored) {}
                    }

                    if (estadoStr != null && !estadoStr.isEmpty() && !estadoStr.equals("undefined")) {
                        try { actualizar.setEstado(Integer.parseInt(estadoStr)); } catch (Exception ignored) {}
                    }

                    dao.actualizar(actualizar);
                }
                response.sendRedirect("DepositoServlet?accion=listar");
                break;
                
            default:
                response.sendRedirect("DepositoServlet?accion=listar");
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