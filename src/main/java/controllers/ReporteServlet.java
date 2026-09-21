package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import dao.ChequeDAO;
import dao.ConciliacionDAO;
import dao.ConciliacionDetalleChequeDAO;
import dao.ConciliacionDetalleDepositoDAO;
import dao.DepositoDAO;
import model.Cheque;
import model.Conciliacion;
import model.Deposito;
import model.Usuario;

@WebServlet("/ReporteServlet")
public class ReporteServlet extends HttpServlet
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
                         || "AUDITOR".equalsIgnoreCase(rol);
        if (!puedeVer) {
            response.sendRedirect("DashboardServlet?error=sinPermiso");
            return;
        }

        String tipo = request.getParameter("tipo");

        if (tipo == null)
        {
            request.getRequestDispatcher("reportes.jsp").forward(request, response);
            return;
        }

        switch (tipo)
        {
            case "cheques":
                ChequeDAO chequeDAO = new ChequeDAO();
                List<Cheque> cheques = chequeDAO.listarTodos();
                BigDecimal totalCheques = BigDecimal.ZERO;
                for (Cheque c : cheques)
                {
                    totalCheques = totalCheques.add(c.getMonto());
                }
                request.setAttribute("cheques", cheques);
                request.setAttribute("totalCheques", totalCheques);
                request.setAttribute("reporteTipo", "cheques");
                request.getRequestDispatcher("reportes.jsp").forward(request, response);
                break;

            case "chequesAnulados":
                ChequeDAO chequeAnuladoDAO = new ChequeDAO();
                List<Cheque> anulados = chequeAnuladoDAO.listarPorEstado(4);
                request.setAttribute("cheques", anulados);
                request.setAttribute("reporteTipo", "chequesAnulados");
                request.getRequestDispatcher("reportes.jsp").forward(request, response);
                break;

            case "depositos":
                DepositoDAO depositoDAO = new DepositoDAO();
                List<Deposito> depositos = depositoDAO.listarTodos();
                BigDecimal totalDepositos = BigDecimal.ZERO;
                for (Deposito d : depositos)
                {
                    totalDepositos = totalDepositos.add(d.getMonto());
                }
                request.setAttribute("depositos", depositos);
                request.setAttribute("totalDepositos", totalDepositos);
                request.setAttribute("reporteTipo", "depositos");
                request.getRequestDispatcher("reportes.jsp").forward(request, response);
                break;

            case "conciliaciones":
                ConciliacionDAO conciliacionDAO = new ConciliacionDAO();
                List<Conciliacion> conciliaciones = conciliacionDAO.listarTodos();
                request.setAttribute("conciliaciones", conciliaciones);
                request.setAttribute("reporteTipo", "conciliaciones");
                request.getRequestDispatcher("reportes.jsp").forward(request, response);
                break;

            case "conciliacionDetalle":
                int idConc = Integer.parseInt(request.getParameter("id"));
                ConciliacionDAO concDAO = new ConciliacionDAO();
                Conciliacion concDetalle = concDAO.obtenerPorId(idConc);
                request.setAttribute("conciliacion", concDetalle);

                ConciliacionDetalleChequeDAO dcDAO = new ConciliacionDetalleChequeDAO();
                ConciliacionDetalleDepositoDAO ddDAO = new ConciliacionDetalleDepositoDAO();
                request.setAttribute("detalleCheques", dcDAO.listarPorConciliacion(idConc));
                request.setAttribute("detalleDepositos", ddDAO.listarPorConciliacion(idConc));

                if (concDetalle != null)
                {
                    ChequeDAO chequeAnuladoDetalleDAO = new ChequeDAO();
                    List<Cheque> anuladosDetalle = chequeAnuladoDetalleDAO.listarAnuladosHastaPeriodo(concDetalle.getPeriodo());
                    BigDecimal totalAnuladosDetalle = BigDecimal.ZERO;
                    for (Cheque c : anuladosDetalle)
                    {
                        totalAnuladosDetalle = totalAnuladosDetalle.add(c.getMonto());
                    }
                    request.setAttribute("totalAnuladosDetalle", totalAnuladosDetalle);
                }

                request.setAttribute("reporteTipo", "conciliacionDetalle");
                request.getRequestDispatcher("reportes.jsp").forward(request, response);
                break;

            default:
                request.getRequestDispatcher("reportes.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
}