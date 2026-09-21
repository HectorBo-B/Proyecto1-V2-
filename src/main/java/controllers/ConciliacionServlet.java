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
import model.ConciliacionDetalleCheque;
import model.ConciliacionDetalleDeposito;
import model.Deposito;
import model.Usuario;

@WebServlet("/ConciliacionServlet")
public class ConciliacionServlet extends HttpServlet
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

        boolean puedeModificar = "ADMIN".equalsIgnoreCase(rol) || "CONTADOR".equalsIgnoreCase(rol);

        String accion = request.getParameter("accion");
        ConciliacionDAO dao = new ConciliacionDAO();

        if (accion == null) accion = "listar";

        switch (accion)
        {
            case "listar":
                List<Conciliacion> lista = dao.listarTodos();
                request.setAttribute("conciliaciones", lista);
                request.getRequestDispatcher("conciliacion.jsp").forward(request, response);
                break;

            case "buscar":
                String texto = request.getParameter("texto");
                List<Conciliacion> resultadosBusqueda = dao.buscarPorPeriodo(texto);
                request.setAttribute("conciliaciones", resultadosBusqueda);
                request.getRequestDispatcher("conciliacion.jsp").forward(request, response);
                break;

            case "nueva":
                if (!puedeModificar) {
                    response.sendRedirect("DashboardServlet?error=sinPermiso");
                    return;
                }
                request.setAttribute("modo", "crear");
                request.getRequestDispatcher("conciliacion.jsp").forward(request, response);
                break;

            case "calcular":
                if (!puedeModificar) {
                    response.sendRedirect("DashboardServlet?error=sinPermiso");
                    return;
                }
                String periodo = request.getParameter("periodo");
                BigDecimal saldoBanco = new BigDecimal(request.getParameter("saldo_banco"));
                BigDecimal saldoLibros = new BigDecimal(request.getParameter("saldo_libros"));

                ChequeDAO chequeDAO = new ChequeDAO();
                List<Cheque> chequesPendientes = chequeDAO.listarNoConciliadosHastaPeriodo(periodo);
                BigDecimal totalPendientes = BigDecimal.ZERO;
                for (Cheque c : chequesPendientes)
                {
                    totalPendientes = totalPendientes.add(c.getMonto());
                }

                List<Cheque> chequesAnulados = chequeDAO.listarAnuladosHastaPeriodo(periodo);
                BigDecimal totalAnulados = BigDecimal.ZERO;
                for (Cheque c : chequesAnulados)
                {
                    totalAnulados = totalAnulados.add(c.getMonto());
                }

                DepositoDAO depositoDAO = new DepositoDAO();
                List<Deposito> depositosTransito = depositoDAO.listarNoConciliadosHastaPeriodo(periodo);
                BigDecimal totalDepositos = BigDecimal.ZERO;
                for (Deposito d : depositosTransito)
                {
                    totalDepositos = totalDepositos.add(d.getMonto());
                }

                BigDecimal saldoConciliadoLibros = saldoLibros
                        .add(totalDepositos)
                        .add(totalAnulados)
                        .subtract(totalPendientes);

                BigDecimal saldoConciliadoBanco = saldoBanco
                        .add(totalDepositos)
                        .subtract(totalPendientes);

                BigDecimal diferencia = saldoConciliadoLibros.subtract(saldoConciliadoBanco);

                request.setAttribute("periodo", periodo);
                request.setAttribute("saldoLibros", saldoLibros);
                request.setAttribute("totalDepositos", totalDepositos);
                request.setAttribute("totalAnulados", totalAnulados);
                request.setAttribute("totalPendientes", totalPendientes);
                request.setAttribute("saldoConciliadoLibros", saldoConciliadoLibros);
                request.setAttribute("saldoBanco", saldoBanco);
                request.setAttribute("saldoConciliadoBanco", saldoConciliadoBanco);
                request.setAttribute("diferencia", diferencia);
                request.setAttribute("chequesPendientes", chequesPendientes);
                request.setAttribute("chequesAnulados", chequesAnulados);
                request.setAttribute("depositosTransito", depositosTransito);
                request.setAttribute("modo", "resultado");

                request.getRequestDispatcher("conciliacion.jsp").forward(request, response);
                break;

            case "obtener":
                int id = Integer.parseInt(request.getParameter("id"));
                Conciliacion c = dao.obtenerPorId(id);
                request.setAttribute("conciliacion", c);
                request.getRequestDispatcher("conciliacion.jsp").forward(request, response);
                break;

            case "ver":
                int idVer = Integer.parseInt(request.getParameter("id"));
                Conciliacion conc = dao.obtenerPorId(idVer);
                request.setAttribute("conciliacion", conc);

                ConciliacionDetalleChequeDAO dcDAO = new ConciliacionDetalleChequeDAO();
                ConciliacionDetalleDepositoDAO ddDAO = new ConciliacionDetalleDepositoDAO();
                request.setAttribute("detalleCheques", dcDAO.listarPorConciliacion(idVer));
                request.setAttribute("detalleDepositos", ddDAO.listarPorConciliacion(idVer));

                request.getRequestDispatcher("reporteConciliacion.jsp").forward(request, response);
                break;

            default:
                response.sendRedirect("ConciliacionServlet?accion=listar");
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
        boolean puedeModificar = "ADMIN".equalsIgnoreCase(rol) || "CONTADOR".equalsIgnoreCase(rol);
        if (!puedeModificar) {
            response.sendRedirect("DashboardServlet?error=sinPermiso");
            return;
        }

        String accion = request.getParameter("accion");
        ConciliacionDAO dao = new ConciliacionDAO();

        if (accion == null)
        {
            response.sendRedirect("ConciliacionServlet?accion=listar");
            return;
        }

        switch (accion)
        {
            case "guardar":
                String periodo = request.getParameter("periodo");
                BigDecimal saldoLibros = new BigDecimal(request.getParameter("saldo_libros"));
                BigDecimal depositosTransito = new BigDecimal(request.getParameter("depositos_transito"));
                BigDecimal chequesPendientes = new BigDecimal(request.getParameter("cheques_pendientes"));
                BigDecimal saldoBanco = new BigDecimal(request.getParameter("saldo_banco"));
                BigDecimal diferencia = new BigDecimal(request.getParameter("diferencia"));
                String observaciones = request.getParameter("observaciones");

                if (diferencia.compareTo(BigDecimal.ZERO) != 0)
                {
                    response.sendRedirect("ConciliacionServlet?accion=nueva&error=diferencia");
                    return;
                }

                Conciliacion nueva = new Conciliacion();
                nueva.setPeriodo(periodo);
                nueva.setSaldoLibros(saldoLibros);
                nueva.setDepositosTransito(depositosTransito);
                nueva.setChequesPendientes(chequesPendientes);
                nueva.setSaldoBanco(saldoBanco);
                nueva.setDiferencia(diferencia);
                nueva.setEstado(3);
                nueva.setIdUsuario(user.getIdUsuario());
                nueva.setObservaciones(observaciones);

                boolean guardado = dao.crear(nueva);

                if (guardado)
                {
                    int idConciliacion = nueva.getIdConciliacion();

                    ChequeDAO chequeDAO = new ChequeDAO();
                    List<Cheque> cheques = chequeDAO.listarNoConciliadosHastaPeriodo(periodo);
                    List<Cheque> anulados = chequeDAO.listarAnuladosHastaPeriodo(periodo);
                    ConciliacionDetalleChequeDAO dcDAO = new ConciliacionDetalleChequeDAO();
                    for (Cheque c : cheques)
                    {
                        ConciliacionDetalleCheque det = new ConciliacionDetalleCheque();
                        det.setIdConciliacion(idConciliacion);
                        det.setIdCheque(c.getIdCheque());
                        det.setMontoAlMomento(c.getMonto());
                        dcDAO.agregarDetalle(det);
                    }
                    for (Cheque c : anulados)
                    {
                        ConciliacionDetalleCheque det = new ConciliacionDetalleCheque();
                        det.setIdConciliacion(idConciliacion);
                        det.setIdCheque(c.getIdCheque());
                        det.setMontoAlMomento(c.getMonto());
                        dcDAO.agregarDetalle(det);
                    }

                    DepositoDAO depositoDAO = new DepositoDAO();
                    List<Deposito> depositos = depositoDAO.listarNoConciliadosHastaPeriodo(periodo);
                    ConciliacionDetalleDepositoDAO ddDAO = new ConciliacionDetalleDepositoDAO();
                    for (Deposito d : depositos)
                    {
                        ConciliacionDetalleDeposito det = new ConciliacionDetalleDeposito();
                        det.setIdConciliacion(idConciliacion);
                        det.setIdDeposito(d.getIdDeposito());
                        det.setMontoAlMomento(d.getMonto());
                        ddDAO.agregarDetalle(det);
                    }
                }

                response.sendRedirect("ConciliacionServlet?accion=listar");
                break;

            default:
                response.sendRedirect("ConciliacionServlet?accion=listar");
        }
    }
}