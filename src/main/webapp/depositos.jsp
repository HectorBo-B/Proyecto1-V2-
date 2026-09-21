<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Usuario, model.Deposito, java.util.List" %>
<%
    Usuario user = (Usuario) session.getAttribute("usuario");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<Deposito> depositos = (List<Deposito>) request.getAttribute("depositos");
    boolean puedeModificar = "ADMIN".equalsIgnoreCase(user.getRol()) 
                           || "CONTADOR".equalsIgnoreCase(user.getRol()) 
                           || "AUXILIAR".equalsIgnoreCase(user.getRol());
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Depósitos - Sistema de Conciliación Bancaria</title>
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
                    <h1>Depósitos</h1>
                    <p>Registro de ingresos bancarios institucionales</p>
                </div>
                <% if (puedeModificar) { %>
                    <button class="btn btn-primary" onclick="abrirModalDeposito()">
                        <i class="fas fa-plus"></i> Registrar depósito
                    </button>
                <% } %>
            </div>

            <div class="card">
                <div class="card-toolbar" style="border-bottom: none; margin-bottom: 0;">
                    <div class="filter-group" style="flex: 0 0 400px; max-width: 400px;">
                        <label>Buscar</label>
                        <input type="text" id="inputBuscar" class="form-control"
                               placeholder="Buscar por comprobante..."
                               onkeyup="buscarAutomatico()">
                    </div>
                    <div class="filter-group" style="flex: 0 0 auto; max-width: none; display:flex; align-items:flex-end;">
                        <a href="#" class="btn btn-secondary" onclick="cargarTodos(); return false;">
                            <i class="fas fa-sync-alt"></i> Limpiar
                        </a>
                    </div>
                </div>
            </div>

            <div class="card">
                <div id="tablaDepositos">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Comprobante</th>
                                    <th>Tipo</th>
                                    <th>Monto</th>
                                    <th>Detalle</th>
                                    <th>Fecha</th>
                                    <th>Estado</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (depositos != null && !depositos.isEmpty()) { %>
                                    <% for (Deposito d : depositos) { %>
                                        <tr>
                                            <td><%= d.getIdDeposito() %></td>
                                            <td><%= d.getNumeroComprobante() %></td>
                                            <td>
                                                <span class="badge <%= d.getTipoDeposito() == 1 ? "badge-transferencia" : "badge-directo" %>">
                                                    <%= d.getTipoDeposito() == 1 ? "TRANSFERENCIA" : "DEP. DIRECTO" %>
                                                </span>
                                            </td>
                                            <td>B/. <%= String.format("%,.2f", d.getMonto().doubleValue()) %></td>
                                            <td><%= d.getDetalle() != null ? d.getDetalle() : "-" %></td>
                                            <td><%= d.getFecha() %></td>
                                            <td>
                                                <span class="badge <%= d.getEstado() == 1 ? "badge-activo" : "badge-inactivo" %>">
                                                    <%= d.getEstado() == 1 ? "ACTIVO" : "INACTIVO" %>
                                                </span>
                                            </td>
                                            <td>
                                                <% if (!puedeModificar) { %>
                                                    <span style="color:#94A3B8; font-size:12px; font-style:italic;">Solo lectura</span>
                                                <% } else { %>
                                                    <button class="btn-icon" onclick="editarDeposito(<%= d.getIdDeposito() %>)" title="Editar">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    <button class="btn-icon"
                                                            onclick="cambiarEstado({
                                                                id: <%= d.getIdDeposito() %>,
                                                                nombre: 'Comprobante <%= d.getNumeroComprobante().replace("'", "\\'") %>',
                                                                etiquetaNombre: 'Comprobante',
                                                                estadoActual: <%= d.getEstado() %>,
                                                                estadoInactivo: 0,
                                                                entidad: 'depósito',
                                                                url: 'DepositoServlet?accion=cambiarEstado'
                                                            })"
                                                            title="<%= d.getEstado() == 1 ? "Poner inactivo" : "Poner activo" %>">
                                                       <i class="fas fa-trash-alt"></i>
                                                    </button>
                                                <% } %>
                                            </td>
                                        </tr>
                                    <% } %>
                                <% } else { %>
                                    <tr>
                                        <td colspan="8" class="text-center">No hay depósitos registrados</td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <!-- MODAL PARA CREAR / EDITAR DEPÓSITO -->
    <div class="modal fade" id="modalDeposito" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="DepositoServlet" method="post" autocomplete="off">
                    <input type="hidden" name="accion" id="formAccion" value="crear">
                    <input type="hidden" name="id" id="formId">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalTitle">Registrar depósito</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label>Tipo de depósito <span class="required">*</span></label>
                            <select name="tipo_deposito" id="formTipoDeposito" class="form-control" required>
                                <option value="">--- Seleccionar tipo ---</option>
                                <option value="1">Transferencia bancaria</option>
                                <option value="2">Depósito directo</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Número de comprobante <span class="required">*</span></label>
                            <input type="text" name="numero_comprobante" id="formNumeroComprobante" class="form-control"
                                   placeholder="Ej. TRANS-2026-001" required>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Fecha <span class="required">*</span></label>
                                    <span id="formFecha" class="form-control"
                                          style="background:#f8f9fa; font-weight:bold; color:#1B3F7A; display:block; width:100%; box-sizing:border-box;"></span>
                                    <input type="hidden" name="fecha" id="formFechaHidden" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Monto (B/.) <span class="required">*</span></label>
                                    <input type="text" name="monto" id="formMonto" class="form-control monto-input"
                                           placeholder="0.00"
                                           oninput="formatearMontoEnVivo(this)"
                                           onblur="completarCentavos(this)"
                                           required>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label>Detalle</label>
                            <textarea name="detalle" id="formDetalle" class="form-control" rows="2"
                                      placeholder="Descripción del depósito..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary" id="btnGuardarDeposito">Guardar depósito</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="js/funciones.js"></script>
    <script>
        function buscarAutomatico() {
            var input = document.getElementById('inputBuscar');
            var texto = input.value.trim();
            if (texto.length >= 1) {
                fetch('DepositoServlet?accion=buscar&texto=' + encodeURIComponent(texto))
                    .then(response => response.text())
                    .then(html => {
                        var parser = new DOMParser();
                        var doc = parser.parseFromString(html, 'text/html');
                        var tabla = doc.querySelector('#tablaDepositos');
                        if (tabla) {
                            document.getElementById('tablaDepositos').innerHTML = tabla.innerHTML;
                        }
                    })
                    .catch(error => console.error('Error:', error));
            } else {
                cargarTodos();
            }
        }
        function cargarTodos() {
            fetch('DepositoServlet?accion=listar')
                .then(response => response.text())
                .then(html => {
                    var parser = new DOMParser();
                    var doc = parser.parseFromString(html, 'text/html');
                    var tabla = doc.querySelector('#tablaDepositos');
                    if (tabla) {
                        document.getElementById('tablaDepositos').innerHTML = tabla.innerHTML;
                    }
                    document.getElementById('inputBuscar').value = '';
                })
                .catch(error => console.error('Error:', error));
        }
        function obtenerFechaActualFormateada() {
            var hoy = new Date();
            var dia = String(hoy.getDate()).padStart(2, '0');
            var mes = String(hoy.getMonth() + 1).padStart(2, '0');
            var anio = hoy.getFullYear();
            return { display : dia + '/' + mes + '/' + anio, iso : anio + '-' + mes + '-' + dia };
        }
        function abrirModalDeposito() {
            document.getElementById('formAccion').value = 'crear';
            document.getElementById('formId').value = '';
            document.getElementById('formTipoDeposito').value = '';
            document.getElementById('formNumeroComprobante').value = '';
            document.getElementById('formMonto').value = '';
            document.getElementById('formDetalle').value = '';
            document.getElementById('modalTitle').textContent = 'Registrar depósito';
            document.getElementById('btnGuardarDeposito').textContent = 'Guardar depósito';

            var fechaActual = obtenerFechaActualFormateada();
            document.getElementById('formFecha').textContent = fechaActual.display;
            document.getElementById('formFechaHidden').value = fechaActual.iso;

            new bootstrap.Modal(document.getElementById('modalDeposito')).show();
        }
        function editarDeposito(id) {
            fetch('DepositoServlet?accion=obtener&id=' + id)
                .then(response => response.json())
                .then(data => {
                    document.getElementById('formAccion').value = 'actualizar';
                    document.getElementById('formId').value = data.id;
                    document.getElementById('formTipoDeposito').value = data.tipo_deposito;
                    document.getElementById('formNumeroComprobante').value = data.numero_comprobante;
                    document.getElementById('formDetalle').value = data.detalle || '';
                    document.getElementById('modalTitle').textContent = 'Editar depósito';
                    document.getElementById('btnGuardarDeposito').textContent = 'Guardar cambios';

                    var montoInput = document.getElementById('formMonto');
                    montoInput.value = data.monto;
                    formatearMontoConComas(montoInput);

                    var fecha = data.fecha;
                    var display = fecha;
                    if (fecha && fecha.length === 10) {
                        var partes = fecha.split('-');
                        display = partes[2] + '/' + partes[1] + '/' + partes[0];
                    }
                    document.getElementById('formFecha').textContent = display;
                    document.getElementById('formFechaHidden').value = fecha;

                    new bootstrap.Modal(document.getElementById('modalDeposito')).show();
                })
                .catch(error => console.error('Error:', error));
        }
    </script>
</body>
</html>