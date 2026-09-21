<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Usuario, model.Conciliacion, model.Cheque, model.Deposito, java.util.List, java.math.BigDecimal" %>
<%
    Usuario user = (Usuario) session.getAttribute("usuario");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<Conciliacion> conciliaciones = (List<Conciliacion>) request.getAttribute("conciliaciones");
    String modo = (String) request.getAttribute("modo");
    String periodo = (String) request.getAttribute("periodo");
    BigDecimal saldoLibros = (BigDecimal) request.getAttribute("saldoLibros");
    BigDecimal totalDepositos = (BigDecimal) request.getAttribute("totalDepositos");
    BigDecimal totalAnulados = (BigDecimal) request.getAttribute("totalAnulados");
    BigDecimal totalPendientes = (BigDecimal) request.getAttribute("totalPendientes");
    BigDecimal saldoConciliadoLibros = (BigDecimal) request.getAttribute("saldoConciliadoLibros");
    BigDecimal saldoBanco = (BigDecimal) request.getAttribute("saldoBanco");
    BigDecimal saldoConciliadoBanco = (BigDecimal) request.getAttribute("saldoConciliadoBanco");
    BigDecimal diferencia = (BigDecimal) request.getAttribute("diferencia");
    List<Cheque> chequesPendientes = (List<Cheque>) request.getAttribute("chequesPendientes");
    List<Cheque> chequesAnulados = (List<Cheque>) request.getAttribute("chequesAnulados");
    List<Deposito> depositosTransito = (List<Deposito>) request.getAttribute("depositosTransito");
    String error = request.getParameter("error");
    
    // ⭐ Solo ADMIN y CONTADOR pueden crear conciliaciones
    boolean puedeModificar = "ADMIN".equalsIgnoreCase(user.getRol()) 
                           || "CONTADOR".equalsIgnoreCase(user.getRol());
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Conciliación - Sistema de Conciliación Bancaria</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/estilos.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="includes/sidebar.jsp" />

        <main class="main-content">
            <div class="page-header">
                <div>
                    <h1>Conciliación Bancaria Mensual</h1>
                    <p>Verificación de saldos contables y bancarios</p>
                </div>
                                <% if (puedeModificar) { %>
                    <a href="ConciliacionServlet?accion=nueva" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Nueva conciliación
                    </a>
                <% } %>
            </div>

            <% if ("diferencia".equals(error)) { %>
                <div class="alert alert-danger">
                    ❌ No se puede guardar una conciliación con diferencia diferente de cero.
                </div>
            <% } %>

            <!-- ============================================================ -->
            <!-- FILTROS -->
            <!-- ============================================================ -->
            <div class="card">
                <div class="card-toolbar" style="border-bottom: none; margin-bottom: 0;">
                    <div class="filter-group" style="flex: 0 0 400px; max-width: 400px;">
                        <label>Buscar</label>
                        <input type="text" id="inputBuscar" class="form-control"
                               placeholder="Buscar por período (Ej. 2026-09)..."
                               onkeyup="buscarAutomatico()">
                    </div>
                    <div class="filter-group" style="flex: 0 0 auto; max-width: none; display:flex; align-items:flex-end;">
                        <a href="#" class="btn btn-secondary" onclick="cargarTodos(); return false;">
                            <i class="fas fa-sync-alt"></i> Limpiar
                        </a>
                    </div>
                </div>
            </div>

            <!-- Tabla de conciliaciones -->
            <div class="card">
                <div id="tablaConciliaciones">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Período</th>
                                    <th>Saldo libros</th>
                                    <th>Depósitos tránsito</th>
                                    <th>Cheques pendientes</th>
                                    <th>Saldo banco</th>
                                    <th>Diferencia</th>
                                    <th>Estado</th>
                                    <th>Fecha</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (conciliaciones != null && !conciliaciones.isEmpty()) { %>
                                    <% for (Conciliacion c : conciliaciones) { %>
                                        <tr>
                                            <td><%= c.getPeriodo() %></td>
                                            <td>B/. <%= String.format("%,.2f", c.getSaldoLibros().doubleValue()) %></td>
                                            <td>B/. <%= String.format("%,.2f", c.getDepositosTransito().doubleValue()) %></td>
                                            <td>B/. <%= String.format("%,.2f", c.getChequesPendientes().doubleValue()) %></td>
                                            <td>B/. <%= String.format("%,.2f", c.getSaldoBanco().doubleValue()) %></td>
                                            <td style="color: <%= c.getDiferencia().doubleValue() == 0 ? "#059669" : "#DC2626" %>; font-weight:600;">
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
                                            <td><%= c.getFechaConciliacion() %></td>
                                        </tr>
                                    <% } %>
                                <% } else { %>
                                    <tr>
                                        <td colspan="8" class="text-center">No hay conciliaciones registradas</td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- ============================================================ -->
            <!-- RESULTADO DEL CÁLCULO -->
            <!-- ============================================================ -->
            <% if ("resultado".equals(modo)) { %>
                <div class="card" style="margin-top:20px;">
                    <h3>Resultado de la conciliación — <%= periodo %></h3>

                    <div class="row">
                        <div class="col-md-6">
                            <h4 style="font-size:14px;">Bloque 1 — Saldo según libros</h4>
                            <div class="conciliacion-resultado">
                                <div class="result-item">
                                    <span class="result-label">Saldo según libros</span>
                                    <span class="result-value">B/. <%= String.format("%,.2f", saldoLibros.doubleValue()) %></span>
                                </div>
                                <div class="result-item">
                                    <span class="result-label">+ Depósitos</span>
                                    <span class="result-value" style="color:#059669;">+ B/. <%= String.format("%,.2f", totalDepositos.doubleValue()) %></span>
                                </div>
                                <div class="result-item">
                                    <span class="result-label">+ Cheques anulados</span>
                                    <span class="result-value" style="color:#059669;">+ B/. <%= String.format("%,.2f", totalAnulados.doubleValue()) %></span>
                                </div>
                                <div class="result-item">
                                    <span class="result-label">− Cheques girados (pendientes)</span>
                                    <span class="result-value" style="color:#DC2626;">− B/. <%= String.format("%,.2f", totalPendientes.doubleValue()) %></span>
                                </div>
                                <div class="result-item" style="border-top:2px solid #D1DBE8;padding-top:10px;">
                                    <span class="result-label" style="font-weight:700;">= Saldo conciliado según libros</span>
                                    <span class="result-value" style="font-weight:700;">B/. <%= String.format("%,.2f", saldoConciliadoLibros.doubleValue()) %></span>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <h4 style="font-size:14px;">Bloque 2 — Saldo en banco</h4>
                            <div class="conciliacion-resultado">
                                <div class="result-item">
                                    <span class="result-label">Saldo según banco</span>
                                    <span class="result-value">B/. <%= String.format("%,.2f", saldoBanco.doubleValue()) %></span>
                                </div>
                                <div class="result-item">
                                    <span class="result-label">+ Depósitos en tránsito</span>
                                    <span class="result-value" style="color:#059669;">+ B/. <%= String.format("%,.2f", totalDepositos.doubleValue()) %></span>
                                </div>
                                <div class="result-item">
                                    <span class="result-label">− Cheques en circulación</span>
                                    <span class="result-value" style="color:#DC2626;">− B/. <%= String.format("%,.2f", totalPendientes.doubleValue()) %></span>
                                </div>
                                <div class="result-item" style="border-top:2px solid #D1DBE8;padding-top:10px;">
                                    <span class="result-label" style="font-weight:700;">= Saldo conciliado igual a banco</span>
                                    <span class="result-value" style="font-weight:700;">B/. <%= String.format("%,.2f", saldoConciliadoBanco.doubleValue()) %></span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="result-item" style="margin-top:15px;">
                        <span class="result-label">Diferencia</span>
                        <span class="result-value" style="color: <%= diferencia.doubleValue() == 0 ? "#059669" : "#DC2626" %>; font-weight:700;">
                            B/. <%= String.format("%,.2f", diferencia.doubleValue()) %>
                        </span>
                    </div>

                    <div class="row" style="margin-top:20px;">
                        <div class="col-md-4">
                            <h4 style="font-size:14px;">Cheques pendientes (<%= chequesPendientes != null ? chequesPendientes.size() : 0 %>)</h4>
                            <div class="table-responsive">
                                <table class="table" style="font-size:13px;">
                                    <thead>
                                        <tr><th>N.º</th><th>Fecha</th><th>Monto</th></tr>
                                    </thead>
                                    <tbody>
                                        <% if (chequesPendientes != null) {
                                            for (Cheque c : chequesPendientes) { %>
                                                <tr>
                                                    <td><%= c.getNumeroCheque() %></td>
                                                    <td><%= c.getFechaCheque() %></td>
                                                    <td>B/. <%= String.format("%,.2f", c.getMonto().doubleValue()) %></td>
                                                </tr>
                                        <%   }
                                        } %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <h4 style="font-size:14px;">Cheques anulados (<%= chequesAnulados != null ? chequesAnulados.size() : 0 %>)</h4>
                            <div class="table-responsive">
                                <table class="table" style="font-size:13px;">
                                    <thead>
                                        <tr><th>N.º</th><th>Fecha anulación</th><th>Monto</th></tr>
                                    </thead>
                                    <tbody>
                                        <% if (chequesAnulados != null) {
                                            for (Cheque c : chequesAnulados) { %>
                                                <tr>
                                                    <td><%= c.getNumeroCheque() %></td>
                                                    <td><%= c.getFechaAnulacion() %></td>
                                                    <td>B/. <%= String.format("%,.2f", c.getMonto().doubleValue()) %></td>
                                                </tr>
                                        <%   }
                                        } %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <h4 style="font-size:14px;">Depósitos en tránsito (<%= depositosTransito != null ? depositosTransito.size() : 0 %>)</h4>
                            <div class="table-responsive">
                                <table class="table" style="font-size:13px;">
                                    <thead>
                                        <tr><th>Comprobante</th><th>Fecha</th><th>Monto</th></tr>
                                    </thead>
                                    <tbody>
                                        <% if (depositosTransito != null) {
                                            for (Deposito d : depositosTransito) { %>
                                                <tr>
                                                    <td><%= d.getNumeroComprobante() %></td>
                                                    <td><%= d.getFecha() %></td>
                                                    <td>B/. <%= String.format("%,.2f", d.getMonto().doubleValue()) %></td>
                                                </tr>
                                        <%   }
                                        } %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                                        <% if (diferencia.doubleValue() == 0) { %>
                        <div class="alert alert-success" style="margin-top:20px;">
                            ✅ Conciliación exitosa — El saldo bancario coincide con el saldo conciliado.
                        </div>
                        <% if (puedeModificar) { %>
                            <form action="ConciliacionServlet" method="post" autocomplete="off" style="margin-top:15px;">
                                ...
                                <button type="submit" class="btn btn-success" style="margin-top:10px;">
                                    <i class="fas fa-save"></i> Guardar conciliación
                                </button>
                            </form>
                        <% } %>
                    <% } else { %>
                        <div class="alert alert-danger" style="margin-top:20px;">
                            ❌ Conciliación no válida — Existe una diferencia de B/. 
                            <%= String.format("%,.2f", Math.abs(diferencia.doubleValue())) %> que debe ser investigada.
                        </div>
                    <% } %>
                </div>
            <% } %>

            <!-- ============================================================ -->
            <!-- FORMULARIO NUEVA CONCILIACIÓN -->
            <!-- ============================================================ -->
            <% if ("crear".equals(modo)) { %>
                <div class="card" style="margin-top:20px;">
                    <h3>Nueva conciliación</h3>
                    <form action="ConciliacionServlet" method="get">
                        <input type="hidden" name="accion" value="calcular">
                        <div class="row">
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Período</label>
                                    <input type="month" name="periodo" class="form-control" required>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Saldo según libros (B/.)</label>
                                    <input type="number" step="0.01" name="saldo_libros" class="form-control" placeholder="0.00" required>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Saldo según banco (B/.)</label>
                                    <input type="number" step="0.01" name="saldo_banco" class="form-control" placeholder="0.00" required>
                                </div>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary" style="margin-top:10px;">
                            <i class="fas fa-calculator"></i> Calcular conciliación
                        </button>
                    </form>
                </div>
            <% } %>
        </main>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // ============================================================
        // BÚSQUEDA AUTOMÁTICA CON AJAX
        // ============================================================

        function buscarAutomatico() {
            var texto = document.getElementById('inputBuscar').value.trim();

            if (texto.length >= 1) {
                fetch('ConciliacionServlet?accion=buscar&texto=' + encodeURIComponent(texto))
                    .then(response => response.text())
                    .then(html => {
                        var parser = new DOMParser();
                        var doc = parser.parseFromString(html, 'text/html');
                        var tabla = doc.querySelector('#tablaConciliaciones');
                        if (tabla) {
                            document.getElementById('tablaConciliaciones').innerHTML = tabla.innerHTML;
                        }
                    })
                    .catch(error => console.error('Error:', error));
            } else {
                cargarTodos();
            }
        }

        function cargarTodos() {
            fetch('ConciliacionServlet?accion=listar')
                .then(response => response.text())
                .then(html => {
                    var parser = new DOMParser();
                    var doc = parser.parseFromString(html, 'text/html');
                    var tabla = doc.querySelector('#tablaConciliaciones');
                    if (tabla) {
                        document.getElementById('tablaConciliaciones').innerHTML = tabla.innerHTML;
                    }
                    document.getElementById('inputBuscar').value = '';
                })
                .catch(error => console.error('Error:', error));
        }
    </script>
</body>
</html>