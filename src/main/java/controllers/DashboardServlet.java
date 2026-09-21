package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;

import dao.ConciliacionDAO;
import model.Conciliacion;
import dao.ChequeDAO;
import dao.DepositoDAO;
import model.Usuario;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet
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

        ChequeDAO chequeDAO = new ChequeDAO();
        DepositoDAO depositoDAO = new DepositoDAO();

        int emitidos = chequeDAO.listarPorEstado(1).size();
        int anulados = chequeDAO.listarPorEstado(4).size();
        int circulacion = chequeDAO.listarPorEstado(5).size();

        BigDecimal totalCheques = BigDecimal.ZERO;
        for (model.Cheque c : chequeDAO.listarTodos())
        {
            totalCheques = totalCheques.add(c.getMonto());
        }

        BigDecimal totalDepositos = BigDecimal.ZERO;
        for (model.Deposito d : depositoDAO.listarActivos())
        {
            totalDepositos = totalDepositos.add(d.getMonto());
        }

        // ⭐ Última conciliación
        ConciliacionDAO conciliacionDAO = new ConciliacionDAO();
        Conciliacion ultimaConciliacion = conciliacionDAO.obtenerUltima();
        request.setAttribute("ultimaConciliacion", ultimaConciliacion);

        request.setAttribute("emitidos", emitidos);
        request.setAttribute("anulados", anulados);
        request.setAttribute("circulacion", circulacion);
        request.setAttribute("totalCheques", totalCheques);
        request.setAttribute("totalDepositos", totalDepositos);
        request.setAttribute("sinPermiso", request.getParameter("error"));

        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}