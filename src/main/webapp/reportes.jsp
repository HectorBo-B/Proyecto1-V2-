<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Usuario, model.Cheque, model.Deposito, model.Conciliacion, model.ConciliacionDetalleCheque, model.ConciliacionDetalleDeposito, java.util.List, java.math.BigDecimal" %>
<%
    Usuario user = (Usuario) session.getAttribute("usuario");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String reporteTipo = (String) request.getAttribute("reporteTipo");
    List<Cheque> cheques = (List<Cheque>) request.getAttribute("cheques");
    List<Deposito> depositos = (List<Deposito>) request.getAttribute("depositos");
    List<Conciliacion> conciliaciones = (List<Conciliacion>) request.getAttribute("conciliaciones");
    BigDecimal totalCheques = (BigDecimal) request.getAttribute("totalCheques");
    BigDecimal totalDepositos = (BigDecimal) request.getAttribute("totalDepositos");

    Conciliacion conciliacionDetalle = (Conciliacion) request.getAttribute("conciliacion");
    List<ConciliacionDetalleCheque> detalleCheques = (List<ConciliacionDetalleCheque>) request.getAttribute("detalleCheques");
    List<ConciliacionDetalleDeposito> detalleDepositos = (List<ConciliacionDetalleDeposito>) request.getAttribute("detalleDepositos");
    BigDecimal totalAnuladosDetalle = (BigDecimal) request.getAttribute("totalAnuladosDetalle");
    if (totalAnuladosDetalle == null) { totalAnuladosDetalle = BigDecimal.ZERO; }

    boolean modoImprimir = "conciliacionDetalle".equals(reporteTipo) && conciliacionDetalle != null;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reportes - Sistema de Conciliación Bancaria</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/estilos.css">

    <style>
        /* ============================================================
           FORMATO IMPRIMIBLE DE LA CONCILIACIÓN
           ============================================================ */
        .reporte-conciliacion {
            background: #FFFFFF;
            max-width: 850px;
            margin: 0 auto;
            padding: 40px;
            border: 1px solid #CBD5E1;
            font-family: Arial, sans-serif;
        }
        .reporte-conciliacion .titulo {
            text-align: center;
            font-size: 16px;
            font-weight: bold;
            margin-bottom: 6px;
            color: #0D1B2A;
        }
        .reporte-conciliacion .subtitulo {
            text-align: center;
            font-size: 12px;
            color: #5A6A7E;
            margin-bottom: 30px;
        }
        .reporte-conciliacion .bloque {
            margin-bottom: 28px;
        }
        .reporte-conciliacion .bloque h3 {
            font-size: 12px;
            font-weight: bold;
            margin-bottom: 14px;
            letter-spacing: 0.5px;
            color: #0D1B2A;
        }
        .reporte-conciliacion .linea {
            display: flex;
            justify-content: space-between;
            font-size: 12px;
            padding: 4px 0;
        }
        .reporte-conciliacion .linea .etiqueta {
            color: #333;
        }
        .reporte-conciliacion .linea .monto {
            font-family: 'Courier New', monospace;
            font-weight: 600;
        }
        .reporte-conciliacion .subtotal {
            border-top: 1px solid #333;
            margin-top: 6px;
            padding-top: 6px;
            font-weight: bold;
        }
        .reporte-conciliacion .total {
            border-top: 2px solid #000;
            border-bottom: 2px solid #000;
            padding: 6px 0;
            font-weight: bold;
            font-size: 13px;
        }
        .reporte-conciliacion .firmas {
            margin-top: 60px;
            display: flex;
            justify-content: space-around;
        }
        .reporte-conciliacion .firma {
            text-align: center;
            font-size: 11px;
        }
        .reporte-conciliacion .firma .linea-firma {
            border-top: 1px solid #000;
            width: 200px;
            margin-bottom: 4px;
        }

        @media print {
            body * { visibility: hidden; }
            .reporte-conciliacion, .reporte-conciliacion * { visibility: visible; }
            .reporte-conciliacion { position: absolute; left: 0; top: 0; width: 100%; border: none; }
            .no-print { display: none !important; }
            body { background: #FFF; padding: 0; }
        }
    </style>
</head>
<body>
    <div class="app-container">
        <jsp:include page="includes/sidebar.jsp" />

        <main class="main-content">

        <% if (modoImprimir) { %>

            <div class="no-print" style="margin-bottom:20px; display:flex; gap:10px;">
                <button onclick="window.print()" class="btn btn-primary">
                    <i class="fas fa-print"></i> Imprimir
                </button>
                <a href="ReporteServlet?tipo=conciliaciones" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Volver
                </a>
            </div>

            <div class="reporte-conciliacion">
                <div class="titulo">CONCILIACIÓN BANCARIA</div>
                <div class="subtitulo">
                    Período: <%= conciliacionDetalle.getPeriodo() %> |
                    Fecha: <%= conciliacionDetalle.getFechaConciliacion() %>
                </div>

                <div class="bloque">
                    <h3>SALDO SEGÚN LIBRO AL CIERRE DEL PERÍODO</h3>

                    <div class="linea">
                        <span class="etiqueta">Saldo según libro</span>
                        <span class="monto">B/. <%= String.format("%,.2f", conciliacionDetalle.getSaldoLibros().doubleValue()) %></span>
                    </div>
                    <div class="linea">
                        <span class="etiqueta">&nbsp;&nbsp;Más: Depósitos</span>
                        <span class="monto">B/. <%= String.format("%,.2f", conciliacionDetalle.getDepositosTransito().doubleValue()) %></span>
                    </div>
                    <div class="linea">
                        <span class="etiqueta">&nbsp;&nbsp;Más: Cheques anulados</span>
                        <span class="monto">B/. <%= String.format("%,.2f", totalAnuladosDetalle.doubleValue()) %></span>
                    </div>
                    <div class="linea subtotal">
                        <span>SUBTOTAL</span>
                        <span class="monto">B/. <%= String.format("%,.2f",
                            conciliacionDetalle.getSaldoLibros()
                                .add(conciliacionDetalle.getDepositosTransito())
                                .add(totalAnuladosDetalle)
                                .doubleValue()) %></span>
                    </div>
                    <div class="linea" style="margin-top:10px;">
                        <span class="etiqueta">&nbsp;&nbsp;Menos: Cheques girados (pendientes)</span>
                        <span class="monto">B/. <%= String.format("%,.2f", conciliacionDetalle.getChequesPendientes().doubleValue()) %></span>
                    </div>
                    <div class="linea total">
                        <span>SALDO CONCILIADO SEGÚN LIBROS</span>
                        <span class="monto">B/. <%= String.format("%,.2f",
                            conciliacionDetalle.getSaldoLibros()
                                .add(conciliacionDetalle.getDepositosTransito())
                                .add(totalAnuladosDetalle)
                                .subtract(conciliacionDetalle.getChequesPendientes())
                                .doubleValue()) %></span>
                    </div>
                </div>

                <div class="bloque">
                    <h3>SALDO EN BANCO AL CIERRE DEL PERÍODO</h3>

                    <div class="linea">
                        <span class="etiqueta">Saldo en banco</span>
                        <span class="monto">B/. <%= String.format("%,.2f", conciliacionDetalle.getSaldoBanco().doubleValue()) %></span>
                    </div>
                    <div class="linea">
                        <span class="etiqueta">&nbsp;&nbsp;Más: Depósitos en tránsito</span>
                        <span class="monto">B/. <%= String.format("%,.2f", conciliacionDetalle.getDepositosTransito().doubleValue()) %></span>
                    </div>
                    <div class="linea">
                        <span class="etiqueta">&nbsp;&nbsp;Menos: Cheques en circulación</span>
                        <span class="monto">B/. <%= String.format("%,.2f", conciliacionDetalle.getChequesPendientes().doubleValue()) %></span>
                    </div>
                    <div class="linea total">
                        <span>SALDO CONCILIADO IGUAL A BANCO</span>
                        <span class="monto">B/. <%= String.format("%,.2f", 
                            conciliacionDetalle.getSaldoBanco()
                                .add(conciliacionDetalle.getDepositosTransito())
                                .subtract(conciliacionDetalle.getChequesPendientes())
                                .doubleValue()) %></span>
                    </div>
                </div>

                <% if (conciliacionDetalle.getObservaciones() != null && !conciliacionDetalle.getObservaciones().isEmpty()) { %>
                    <div class="bloque">
                        <h3>OBSERVACIONES</h3>
                        <p style="font-size: 12px;"><%= conciliacionDetalle.getObservaciones() %></p>
                    </div>
                <% } %>

                <div class="firmas">
                    <div class="firma">
                        <div class="linea-firma"></div>
                        Verificador (Contador)
                    </div>
                    <div class="firma">
                        <div class="linea-firma"></div>
                        Revisado (Director)
                    </div>
                </div>
            </div>

        <% } else { %>

            <div class="page-header">
                <div>
                    <h1>Consultas y Reportes</h1>
                    <p>Seleccione un reporte para consultar y exportar</p>
                </div>
            </div>

            <div class="reportes-grid">
                <a href="ReporteServlet?tipo=chequesAnulados" class="reporte-card <%= "chequesAnulados".equals(reporteTipo) ? "active" : "" %>">
                    <div class="reporte-icon" style="background:#FFF1F2;color:#DC2626;">
                    <i class="fa-solid fa-ban"></i></div>
                    <span>Cheques anulados</span>
                </a>
                <a href="ReporteServlet?tipo=depositos" class="reporte-card <%= "depositos".equals(reporteTipo) ? "active" : "" %>">
                    <div class="reporte-icon" style="background:#F0FDF4;color:#059669;">
                    <i class="fa-solid fa-sack-dollar"></i></div>
                    <span>Depósitos por período</span>
                </a>
                <a href="ReporteServlet?tipo=conciliaciones" class="reporte-card <%= "conciliaciones".equals(reporteTipo) ? "active" : "" %>">
                    <div class="reporte-icon" style="background:#F5F3FF;color:#7C3AED;">
                    <i class="fa-solid fa-chart-column"></i></div>
                    <span>Conciliaciones realizadas</span>
                </a>
            </div>

            <% if (reporteTipo != null) { %>
                <div class="card" style="margin-top:20px;">
                <div class="card-header-custom">
                        <h3>
                            <% if ("cheques".equals(reporteTipo)) { %>Cheques emitidos por período<% } %>
                            <% if ("chequesAnulados".equals(reporteTipo)) { %>Cheques anulados<% } %>
                            <% if ("depositos".equals(reporteTipo)) { %>Depósitos por período<% } %>
                            <% if ("conciliaciones".equals(reporteTipo)) { %>Conciliaciones realizadas<% } %>
                        </h3>
                        <% if (!"chequesAnulados".equals(reporteTipo) 
                               && !"depositos".equals(reporteTipo) 
                               && !"conciliaciones".equals(reporteTipo)) { %>
                            <div>
                                <button class="btn btn-secondary" onclick="window.print()">🖨️ Imprimir</button>
                            </div>
                        <% } %>
                    </div>

                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <% if ("cheques".equals(reporteTipo) || "chequesAnulados".equals(reporteTipo)) { %>
                                        <th>N.º Cheque</th>
                                        <th>Fecha</th>
                                        <th>Beneficiario</th>
                                        <th>Monto</th>
                                        <th>Estado</th>
                                    <% } else if ("depositos".equals(reporteTipo)) { %>
                                        <th>Comprobante</th>
                                        <th>Tipo</th>
                                        <th>Fecha</th>
                                        <th>Monto</th>
                                        <th>Detalle</th>
                                    <% } else if ("conciliaciones".equals(reporteTipo)) { %>
                                        <th>Período</th>
                                        <th>Saldo libros</th>
                                        <th>Saldo banco</th>
                                        <th>Diferencia</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    <% } %>
                                </tr>
                            </thead>
                            <tbody>
                                <% if ("cheques".equals(reporteTipo) || "chequesPendientes".equals(reporteTipo) || "chequesAnulados".equals(reporteTipo)) { %>
                                    <% if (cheques != null && !cheques.isEmpty()) { %>
                                        <% for (Cheque c : cheques) { %>
                                            <tr>
                                                <td><%= c.getNumeroCheque() %></td>
                                                <td><%= c.getFechaCheque() %></td>
                                                <td><%= c.getNombreProveedor() != null ? c.getNombreProveedor() : "-" %></td>
                                                <td>B/. <%= String.format("%,.2f", c.getMonto().doubleValue()) %></td>
                                                <td>
                                                    <span class="badge <%= 
                                                        c.getEstado() == 4 ? "badge-anulado" :
                                                        c.getEstado() == 5 ? "badge-circulacion" :
                                                        "badge-emitido" %>">
                                                        <%= 
                                                            c.getEstado() == 4 ? "ANULADO" :
                                                            c.getEstado() == 5 ? "CIRCULACIÓN" :
                                                            "EMITIDO" %>
                                                    </span>
                                                </td>
                                            </tr>
                                        <% } %>
                                        <tr class="table-total">
                                            <td colspan="3"><strong>TOTAL</strong></td>
                                            <td><strong>B/. <%= String.format("%,.2f", totalCheques != null ? totalCheques.doubleValue() : 0) %></strong></td>
                                            <td></td>
                                        </tr>
                                    <% } else { %>
                                        <tr><td colspan="5" class="text-center">No hay cheques para mostrar</td></tr>
                                    <% } %>

                                <% } else if ("depositos".equals(reporteTipo)) { %>
                                    <% if (depositos != null && !depositos.isEmpty()) { %>
                                        <% for (Deposito d : depositos) { %>
                                            <tr>
                                                <td><%= d.getNumeroComprobante() %></td>
                                                <td>
                                                    <span class="badge <%= d.getTipoDeposito() == 1 ? "badge-transferencia" : "badge-directo" %>">
                                                        <%= d.getTipoDeposito() == 1 ? "TRANSFERENCIA" : "DEP. DIRECTO" %>
                                                    </span>
                                                </td>
                                                <td><%= d.getFecha() %></td>
                                                <td>B/. <%= String.format("%,.2f", d.getMonto().doubleValue()) %></td>
                                                <td><%= d.getDetalle() != null ? d.getDetalle() : "-" %></td>
                                            </tr>
                                        <% } %>
                                        <tr class="table-total">
                                            <td colspan="3"><strong>TOTAL</strong></td>
                                            <td><strong>B/. <%= String.format("%,.2f", totalDepositos != null ? totalDepositos.doubleValue() : 0) %></strong></td>
                                            <td></td>
                                        </tr>
                                    <% } else { %>
                                        <tr><td colspan="5" class="text-center">No hay depósitos para mostrar</td></tr>
                                    <% } %>

                                <% } else if ("conciliaciones".equals(reporteTipo)) { %>
                                    <% if (conciliaciones != null && !conciliaciones.isEmpty()) { %>
                                        <% for (Conciliacion c : conciliaciones) { %>
                                            <tr>
                                                <td><%= c.getPeriodo() %></td>
                                                <td>B/. <%= String.format("%,.2f", c.getSaldoLibros().doubleValue()) %></td>
                                                <td>B/. <%= String.format("%,.2f", c.getSaldoBanco().doubleValue()) %></td>
                                                <td style="color: <%= c.getDiferencia().doubleValue() == 0 ? "#059669" : "#DC2626" %>">
                                                    B/. <%= String.format("%,.2f", c.getDiferencia().doubleValue()) %>
                                                </td>
                                                <td>
                                                    <span class="badge <%= 
                                                        c.getEstado() == 1 ? "badge-pendiente" :
                                                        c.getEstado() == 2 ? "badge-emitido" :
                                                        c.getEstado() == 3 ? "badge-cobrado" :
                                                        "badge-circulacion" %>">
                                                        <%= 
                                                            c.getEstado() == 1 ? "EN PROCESO" :
                                                            c.getEstado() == 2 ? "AJUSTADA" :
                                                            c.getEstado() == 3 ? "VERIFICADA" :
                                                            "CERRADA" %>
                                                    </span>
                                                </td>
                                                <td>
                                                    <a href="ReporteServlet?tipo=conciliacionDetalle&id=<%= c.getIdConciliacion() %>" 
                                                       class="btn-icon" title="Ver">
                                                        <i class="fa-solid fa-eye"></i>
                                                    </a>
                                                </td>
                                            </tr>
                                        <% } %>
                                    <% } else { %>
                                        <tr><td colspan="6" class="text-center">No hay conciliaciones para mostrar</td></tr>
                                    <% } %>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            <% } else { %>
                <div class="card" style="margin-top:20px;text-align:center;padding:40px;">
                    <p style="color:#5A6A7E;font-size:16px;">Seleccione un reporte de las tarjetas superiores para visualizar los datos.</p>
                </div>
            <% } %>

        <% } %>
        </main>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>